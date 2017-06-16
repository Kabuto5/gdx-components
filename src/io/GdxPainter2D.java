package io;

import helpers.ShaderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import components.interfaces.GdxComponent;
import effects.GdxVisualEffect;

public class GdxPainter2D implements GdxPainter {
  public enum BlendingType { NORMAL, ADDITIVE }
  public enum AlphaBlending { NORMAL, ADDITIVE }
  
  public static final BlendingType BLENDING_TYPE_NORMAL = BlendingType.NORMAL;
  public static final BlendingType BLENDING_TYPE_ADDITIVE = BlendingType.ADDITIVE;
  /** 
   * Correct blending method for rendering (partially) transparent images with naturally looking results.
   **/
  public static final AlphaBlending ALPHA_BLENDING_NORMAL = AlphaBlending.NORMAL;
  /**
   * Quicker blending method, suitable for drawing on opaque background or drawing images consisisting
   * only of fully transparent or fully opaque pixels.
   */
  public static final AlphaBlending ALPHA_BLENDING_ADDITIVE = AlphaBlending.ADDITIVE;

  protected final SpriteBatch spriteBatch = new SpriteBatch();
  
  private float canvasWidth, canvasHeight;
  private int screenWidth, screenHeight;
  private Color mainClearColor = Color.BLACK;
  private boolean blackBorders = true;
  private final OrthographicCamera mainCamera;
  private OrthographicCamera frameCamera;
  private Camera currentCamera;
  private LinkedList<Rectangle> clippingAreas = new LinkedList<Rectangle>();
  private HashMap<TextureKey, ExtendedFrameBuffer> textureFrameBuffers = 
      new HashMap<TextureKey, ExtendedFrameBuffer>(); 
  private ExtendedFrameBuffer shaderFrameBuffer, otherFrameBuffer;
  private ExtendedFrameBuffer currentFrameBuffer;
  private LinkedList<SavedFrameBuffer> frameBufferStack = new LinkedList<SavedFrameBuffer>();
  private ExtendedShapeRenderer shapeRenderer = null;
  private final GlyphLayout glyphLayout = new GlyphLayout();
  private ShaderProgram premultiplyShader, demultiplyShader;
  private ArrayList<GdxVisualEffect> currentVisualEffects = new ArrayList<GdxVisualEffect>(10);
  private ArrayList<GdxShader> currentShaders = new ArrayList<GdxShader>(10);
  private int srcBlendColorFunc, dstBlendColorFunc;
  private int srcBlendAlphaFunc, dstBlendAlphaFunc;
  private Rectangle shaderArea;
  private GdxSpecialCamera specialCamera;
  private boolean scissorStackEmpty = true;
   
  public GdxPainter2D(float canvasWidth, float canvasHeight) {
    this.canvasWidth = canvasWidth;
    this.canvasHeight = canvasHeight;
    screenWidth = Gdx.graphics.getWidth();
    screenHeight = Gdx.graphics.getHeight();
    mainCamera = new OrthographicCamera();
    mainCamera.setToOrtho(true);
    frameCamera = new OrthographicCamera();
    currentCamera = mainCamera;
    updateCamera();
    shaderArea = new Rectangle(0, 0, screenWidth, screenHeight);
    spriteBatch.setBlendFunction(-1, -1); // Prevents SpriteBatch from overriding global OpenGL blending functions
    resetBlendFunction(); // Initializes blending functions
    ShaderProgram.pedantic = false;
  }
  
  protected void clearScreen() {
    Gdx.gl.glClearColor(mainClearColor.r, mainClearColor.g, mainClearColor.b, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }
  
  @Override
  public void begin() {
    resetBlendFunction();
    clearScreen();
    spriteBatch.begin();
    spriteBatch.setProjectionMatrix(currentCamera.combined);
    if (blackBorders) pushClippingArea(0, 0, canvasWidth, canvasHeight);
    beginShaders();
  }
  
  @Override
  public void end() {
    endShaders();
    spriteBatch.end();
    while (!scissorStackEmpty) popClippingArea();
  }
  
  @Override
  public void flush() {
    spriteBatch.flush();
  }
  
  public ShapeRenderer beginShapeBatch(ShapeType shapeType) {
    if (shapeRenderer == null) shapeRenderer = new ExtendedShapeRenderer();
    shapeRenderer.begin(shapeType);
    return shapeRenderer;
  }
  
  public ShapeRenderer beginShapeBatch(ShapeType shapeType, Color color) {
    ShapeRenderer shapeRenderer = beginShapeBatch(shapeType);
    shapeRenderer.setColor(color);
    return shapeRenderer;
  }
  
  public void endShapeBatch() {
    shapeRenderer.end();
  }
  
  public ShapeRenderer getShapeRenderer() {
    if (shapeRenderer == null) shapeRenderer = new ExtendedShapeRenderer();
    return shapeRenderer;
  }
  
  protected ShaderProgram getPremultiplyShader() {
    if (premultiplyShader == null) {
      premultiplyShader = ShaderFactory.createShaderProgram(GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_PREMULTIPLY);
    }
    return premultiplyShader;
  }
  
  protected ShaderProgram getDemultiplyShader() {
    if (demultiplyShader == null) {
      demultiplyShader = ShaderFactory.createShaderProgram(GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_DEMULTIPLY);
    }
    return demultiplyShader;
  }
  
  @Override
  public void setSpecialCamera(GdxSpecialCamera specialCamera) {
    this.specialCamera = specialCamera;
    if (currentFrameBuffer == null) {
      if (specialCamera == null) {
        currentCamera = mainCamera;
      } else {
        currentCamera = specialCamera.getCamera();
        specialCamera.onResize(canvasWidth, canvasHeight, screenWidth, screenHeight, mainCamera.zoom);
      }
      spriteBatch.setProjectionMatrix(currentCamera.combined);
    }
  }

  @Override
  public GdxSpecialCamera getSpecialCamera() {
    return specialCamera;
  }

  protected void updateCamera() {
    mainCamera.viewportWidth = screenWidth;
    mainCamera.viewportHeight = screenHeight;
    mainCamera.zoom = Math.max(canvasWidth / screenWidth, canvasHeight / screenHeight);
    mainCamera.position.x = canvasWidth / 2;
    mainCamera.position.y = canvasHeight / 2;
    mainCamera.update();
    if (specialCamera != null) {
      specialCamera.onResize(canvasWidth, canvasHeight, screenWidth, screenHeight, mainCamera.zoom);
    }
  }

  public int getBlendDstColorFunc() {
    return dstBlendColorFunc;
  }

  public int getBlendSrcColorFunc() {
    return srcBlendColorFunc;
  }

  public int getBlendDstAlphaFunc() {
    return dstBlendAlphaFunc;
  }

  public int getBlendSrcAlphaFunc() {
    return srcBlendAlphaFunc;
  }

  public void setBlendDstColorFunc(int dstColorFunc) {
    setBlendColorFunction(srcBlendColorFunc, dstColorFunc);
  }

  public void setBlendSrcColorFunc(int srcColorFunc) {
    setBlendColorFunction(srcColorFunc, dstBlendColorFunc);
  }

  public void setBlendDstAlphaFunc(int dstAlphaFunc) {
    setBlendAlphaFunction(srcBlendAlphaFunc, dstAlphaFunc);
  }

  public void setBlendSrcAlphaFunc(int srcAlphaFunc) {
    setBlendAlphaFunction(srcAlphaFunc, dstBlendAlphaFunc);
  }
  
  public void setBlendFunction(int srcFunc, int dstFunc) {
    srcBlendColorFunc = srcBlendAlphaFunc = srcFunc;
    dstBlendColorFunc = dstBlendAlphaFunc = dstFunc;
    // Pass it directly to low level in order to affect ShapeRenderer as well
    Gdx.gl.glBlendFunc(srcFunc, dstFunc);
  }

  public void setBlendFunction(int srcColorFunc, int dstColorFunc, int srcAlphaFunc, int dstAlphaFunc) {
    srcBlendColorFunc = srcColorFunc;
    srcBlendAlphaFunc = srcAlphaFunc;
    dstBlendColorFunc = dstColorFunc;
    dstBlendAlphaFunc = dstAlphaFunc;
    Gdx.gl.glBlendFuncSeparate(srcColorFunc, dstColorFunc, srcAlphaFunc, dstAlphaFunc);
  }

  public void setBlendColorFunction(int srcColorFunc, int dstColorFunc) {
    srcBlendColorFunc = srcColorFunc;
    dstBlendColorFunc = dstColorFunc;
    Gdx.gl.glBlendFuncSeparate(srcColorFunc, dstColorFunc, srcBlendAlphaFunc, dstBlendAlphaFunc);
  }

  public void setBlendAlphaFunction(int srcAlphaFunc, int dstAlphaFunc) {
    srcBlendAlphaFunc = srcAlphaFunc;
    dstBlendAlphaFunc = dstAlphaFunc;
    Gdx.gl.glBlendFuncSeparate(srcBlendColorFunc, dstBlendColorFunc, srcAlphaFunc, dstAlphaFunc);
  }

  public void setBlendingType(BlendingType type) {
    boolean normalAlphaBlending = spriteBatch.getShader() == getPremultiplyShader();
    if (type == BlendingType.ADDITIVE) {
      if (normalAlphaBlending) {
        // Part of the correct blending formula, requires alpha premultiplication to work
        setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
      } else {
        // Simplified blending formula for opaque background or binary transparency
        setBlendFunction(
            GL20.GL_SRC_ALPHA, GL20.GL_ONE,
            GL20.GL_ONE, GL20.GL_ONE); // Prevents opacity reduction in renderbuffers with alpha
      }
    } else {
      if (normalAlphaBlending) {
        // Part of the correct blending formula, requires alpha premultiplication to work
        setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
      } else {
        // Simplified blending formula for opaque background or binary transparency
        setBlendFunction(
            GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ONE, GL20.GL_ONE); // Prevents opacity reduction in renderbuffers with alpha
      }
    }
  }

  /**
   * Resets blending function to default. Use in case you modified it via
   * #setBlendFunction(int, int) or so.
   */
  public void resetBlendFunction() {
    setBlendingType(BlendingType.NORMAL);
  }

  @Override
  public void setSize(float width, float height) {
    if (canvasWidth != width || canvasHeight != height) {
      canvasWidth = width;
      canvasHeight = height;
      updateCamera();
    }
  }
  
  public float getWidth() {
    return canvasWidth;
  }
  
  public float getHeight() {
    return canvasHeight;
  }

  @Override
  public void setScreenSize(int screenWidth, int screenHeight) {
    if (this.screenWidth != screenWidth || this.screenHeight != screenHeight) {
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
      updateCamera();
    }
  }

  @Override
  public int getScreenWidth() {
    return screenWidth;
  }
 
  @Override
  public int getScreenHeight() {
    return screenHeight;
  }
  
  @Override
  public int getOriginLeft() {
    return Math.round(screenWidth * 0.5f - mainCamera.position.x / mainCamera.zoom);
  }
  
  @Override
  public int getOriginTop() {
    return Math.round(screenHeight * 0.5f - mainCamera.position.y / mainCamera.zoom);
  }
  
  @Override
  public Ray getPickingRay(int screenX, int screenY) {
    return currentCamera.getPickRay(screenX, screenY);
  }
  
  @Override
  public float getScale() {
    return mainCamera.zoom;
  }
  
  @Override
  public float getCanvasUnitsPerPixel() {
    return mainCamera.zoom;
  }
  
  @Override
  public float getCupp() {
    return mainCamera.zoom;
  }
  
  @Override
  public float getPixelsPerCanvasUnit() {
    return 1 / mainCamera.zoom;
  }
  
  @Override
  public float getPpcu() {
    return 1 / mainCamera.zoom;
  }

  @Override
  public void enableBlending() {
    spriteBatch.enableBlending();
  }

  @Override
  public void disableBlending() {
    spriteBatch.disableBlending();
  }

  @Override
  public void setColor(Color tint) {
    spriteBatch.setColor(tint);
  }

  @Override
  public void setColor(float color) {
    spriteBatch.setColor(color);
  }

  @Override
  public void setColor(float r, float g, float b, float a) {
    spriteBatch.setColor(r, g, b, a);
  }

  @Override
  public Color getColor() {
    return spriteBatch.getColor();
  }

  public void setAlpha(float a) {
    Color color = getColor();
    color.a = a;
    setColor(color);
  }
  
  public void addAlpha(float a) {
    Color color = getColor();
    color.a += a;
    setColor(color);
  }

  public void mulAlpha(float a) {
    Color color = getColor();
    color.a *= a;
    setColor(color);
  }

  public void subAlpha(float a) {
    Color color = getColor();
    color.a -= a;
    setColor(color);
  }

  public void addColor(float r, float g, float b, float a) {
    setColor(getColor().add(r, g, b, a));
  }

  public void addColor(Color color) {
    setColor(getColor().add(color));
  }

  public void mulColor(float r, float g, float b, float a) {
    setColor(getColor().mul(r, g, b, a));
  }

  public void mulColor(Color color) {
    setColor(getColor().mul(color));
  }

  public void subColor(float r, float g, float b, float a) {
    setColor(getColor().sub(r, g, b, a));
  }

  public void subColor(Color color) {
    setColor(getColor().sub(color));
  }
  
  @Override
  public void setClearColor(Color clearColor) {
    this.mainClearColor = clearColor;
  }
  
  @Override
  public Color getClearColor() {
    return mainClearColor;
  }

  @Override
  public void setBlackBorders(boolean blackBorders) {
    this.blackBorders = blackBorders;
  }
  
  @Override
  public boolean isBlackBorders() {
    return blackBorders;
  }
  
  protected ExtendedFrameBuffer createFrameBuffer(float width, float height) {
    return new ExtendedFrameBuffer(Format.RGBA8888, width, height, false);
  }
  
  /**
   * Ensures that a framebuffer exists and has suitable dimensions.
   * <p>
   * If a given framebuffer is not big enough or NULL is given instead, creates and 
   * returns a new framebuffer instance. Otherwise, it simply returns a given instance.
   * @param frameBuffer Candidate for reuse or NULL
   * @param width Width or the framebuffer in canvas units
   * @param height Height or the framebuffer in canvas units
   * @return Framebuffer instance prepared for rendering
   */
  protected ExtendedFrameBuffer prepareFrameBuffer(ExtendedFrameBuffer frameBuffer, float width, float height) {
    if (frameBuffer != null) {
      if (frameBuffer.getCanvasWidth() < width || frameBuffer.getCanvasHeight() < height) {
        frameBuffer.dispose();
        frameBuffer = createFrameBuffer(width, height);
        Gdx.app.log(TAG, String.format("Framebuffer replaced (%.2f x %.2f cu)", width, height));
      } else {
        frameBuffer.setTextureSize(width, height);
      }
    } else {
      frameBuffer = createFrameBuffer(width, height);
      Gdx.app.log(TAG, String.format("Framebuffer created (%.2f x %.2f cu)", width, height));
    }
    return frameBuffer;
  }
  
  protected void beginBuffer(ExtendedFrameBuffer frameBuffer) {
    spriteBatch.end();
    if (currentFrameBuffer != null) {
      currentFrameBuffer.end();
      frameBufferStack.addLast(new SavedFrameBuffer(currentFrameBuffer, currentCamera));
      Gdx.app.log(TAG, "Framebuffer put on stack");
      frameCamera = new OrthographicCamera();
    }
    frameCamera.setToOrtho(true, frameBuffer.getWidth() * getCupp(), frameBuffer.getHeight() * getCupp());
    frameCamera.update();
    currentCamera = frameCamera;
    currentFrameBuffer = frameBuffer;
    spriteBatch.setProjectionMatrix(currentCamera.combined);
    currentFrameBuffer.begin();
    spriteBatch.begin();
  }
  
  protected void endBuffer() {
    spriteBatch.end();
    currentFrameBuffer.end();
    if (frameBufferStack.isEmpty()) {
      currentCamera = specialCamera == null ? mainCamera : specialCamera.getCamera();
      currentFrameBuffer = null;
    } else {
      SavedFrameBuffer savedFrameBuffer = frameBufferStack.removeLast();
      Gdx.app.log(TAG, "Framebuffer taken from stack");
      currentCamera = frameCamera = (OrthographicCamera)savedFrameBuffer.camera;
      currentFrameBuffer = savedFrameBuffer.frameBuffer;
      currentFrameBuffer.begin();
    }
    spriteBatch.begin();
    spriteBatch.setProjectionMatrix(currentCamera.combined);
  }
  
  /**
   * Allows a component to prerender any texture during painting of the component itself. This way, 
   * the component can prepare any complex image in a separate buffer and then simply paint it as 
   * a single texture.
   * <p>
   * Width and height of requested texture are given in internal units. Therefore, if you want 
   * to cover your component by texture, you should pass your component's width and height.
   * <p>
   * In theory, component used for rendering a texture does not need to be the same as a component 
   * calling this method.
   * <p>
   * Calling this method outside a {@link GdxComponent#paint(float, float, GdxPainter) paint} method 
   * will cause a rendering error.
   * <p>
   * Calling this method will reset blending function to default.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @param clearColor Color used to clear a buffer before rendering (can be transparent).
   * @param alphaBlending Blending method used for rendering (partially) transparent textures. 
   *                      If clear color is fully opaque, this parameter is ignored.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height, 
      Color clearColor, AlphaBlending alphaBlending) {
    if (clearColor.a == 1) {
      alphaBlending = AlphaBlending.ADDITIVE;
    }
    endShaders();
    ArrayList<Rectangle> savedScissors = null;
    if (!scissorStackEmpty) {
      flush();
      savedScissors = new ArrayList<Rectangle>(10);
      while (Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST)) {
        savedScissors.add(ScissorStack.popScissors());
      }
    }
    TextureKey textureKey = new TextureKey(component, id);
    ExtendedFrameBuffer frameBuffer = textureFrameBuffers.get(textureKey);
    frameBuffer = prepareFrameBuffer(frameBuffer, width, height);
    textureFrameBuffers.put(textureKey, frameBuffer);
    beginBuffer(frameBuffer);
    Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    if (alphaBlending == AlphaBlending.NORMAL) {
      setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
      spriteBatch.setShader(getPremultiplyShader());
    }
    component.renderTexture(id, width, height, this);
    endBuffer();
    TextureRegion renderedTexture;
//    Texture bufferTexture;
//    int textureWidth = (int)(width * getPpcu());
//    int textureHeight = (int)(height * getPpcu());
    if (alphaBlending == AlphaBlending.NORMAL) {
      otherFrameBuffer = prepareFrameBuffer(otherFrameBuffer, width, height);
      beginBuffer(otherFrameBuffer);
      spriteBatch.setShader(getDemultiplyShader());
      resetBlendFunction();
      disableBlending();
      frameBuffer.drawTexture(0, 0);
//      bufferTexture = textureFrameBuffer.getColorBufferTexture();
//      draw(bufferTexture, 0, 0, width, height, 
//          0, (int)(bufferTexture.getHeight() - textureHeight), 
//          (int)(width * getPpcu()), (int)(height * getPpcu()),
//          false, false);
      endBuffer();
      spriteBatch.setShader(null);
      enableBlending();
      renderedTexture = otherFrameBuffer.getTexture();
//      bufferTexture = otherFrameBuffer.getColorBufferTexture();
    } else {
      renderedTexture = frameBuffer.getTexture();
//      bufferTexture = textureFrameBuffer.getColorBufferTexture();
    }
    if (savedScissors != null) {
      for (int i = savedScissors.size() - 1; i >= 0; i--) {
        ScissorStack.pushScissors(savedScissors.get(i));
      }
    }
    beginShaders();
//    TextureRegion renderedTexture = new TextureRegion(bufferTexture, 
//        0, (int)(bufferTexture.getHeight() - textureHeight), textureWidth, textureHeight);
    return renderedTexture;
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which always uses the most reliable and most expensive blending method.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @return Texture region covering a rendered texture.
   */
  @Override
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height, Color clearColor) {
    return requestTextureRender(component, id, width, height, clearColor, AlphaBlending.NORMAL);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which always uses a transparent color to clear a canvas.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @param alphaBlending Blending method used for rendering (partially) transparent textures. 
   *                      If clear color is fully opaque, this parameter is ignored.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height, AlphaBlending alphaBlending) {
    return requestTextureRender(component, id, width, height, COLOR_TRANSPARENT, alphaBlending);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which always uses a transparent color to clear a canvas and always uses the most reliable and most
   * expensive blending method.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @return Texture region covering a rendered texture.
   */
  @Override
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height) {
    return requestTextureRender(component, id, width, height, COLOR_TRANSPARENT, AlphaBlending.NORMAL);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which automatically initializes its size to size of the component responsible for rendering.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param clearColor Color used to clear a buffer before rendering (can be transparent).
   * @param alphaBlending Blending method used for rendering (partially) transparent textures. 
   *                      If clear color is fully opaque, this parameter is ignored.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, Color clearColor, AlphaBlending alphaBlending) {
    return requestTextureRender(component, id, component.getWidth(), component.getHeight(), clearColor, alphaBlending);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which automatically initializes its size to size of the component responsible for rendering and always 
   * picks the cheapest blending method available while preserving the natural blending results in all situations.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param clearColor Color used to clear a buffer before rendering (can be transparent).
   * @return Texture region covering a rendered texture.
   */
  @Override
  public TextureRegion requestTextureRender(GdxComponent component, int id, Color clearColor) {
    return requestTextureRender(component, id, component.getWidth(), component.getHeight(), clearColor, AlphaBlending.NORMAL);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which automatically initializes its size to size of the component responsible for rendering and always uses 
   * a transparent color to clear a canvas.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param alphaBlending Blending method used for rendering (partially) transparent textures. 
   *                      If clear color is fully opaque, this parameter is ignored.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, AlphaBlending alphaBlending) {
    return requestTextureRender(component, id, component.getWidth(), component.getHeight(), COLOR_TRANSPARENT, alphaBlending);
  }
  
  /**
   * Variant of io.GdxPainter2D#requestTextureRender(GdxComponent, int, float, float, Color, AlphaBlending)
   * which automatically initializes its size to size of the component responsible for rendering, always uses 
   * a transparent color to clear a canvas and always uses the most reliable and most expensive blending method.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @return Texture region covering a rendered texture.
   */
  @Override
  public TextureRegion requestTextureRender(GdxComponent component, int id) {
    return requestTextureRender(component, id, component.getWidth(), component.getHeight(), COLOR_TRANSPARENT, AlphaBlending.NORMAL);
  }
  
  @Override
  public boolean pushClippingArea(float x, float y, float width, float height) {
    return pushClippingArea(new Rectangle(x, y, width, height));
  }
  
  @Override
  public boolean pushClippingArea(Rectangle area) {
    flush();
    Rectangle scissors = new Rectangle();
    ScissorStack.calculateScissors(currentCamera, spriteBatch.getTransformMatrix(), area, scissors);
    boolean scissorsPushed = ScissorStack.pushScissors(scissors);
    if (scissorsPushed) clippingAreas.add(area);
    scissorStackEmpty = !Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST);
    return scissorsPushed;
  }
  
  @Override
  public Rectangle popClippingArea() {
    flush();
    ScissorStack.popScissors();
    scissorStackEmpty = !Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST);
    return clippingAreas.removeLast();
  }
  
  private void moveClippingAreas(float x, float y) {
    for (int i = clippingAreas.size() - 1; i >= 0; i--) {
      Rectangle area = clippingAreas.get(i);
      area.x += x;
      area.y += y;
    }
  }
  
  private boolean recalculateClippingAreas() {
    for (int i = clippingAreas.size(); i > 0; i--) {
      ScissorStack.popScissors();
    }
    for (Rectangle area : clippingAreas) {
      Rectangle scissors = new Rectangle();
      ScissorStack.calculateScissors(currentCamera, spriteBatch.getTransformMatrix(), area, scissors);
      if (!ScissorStack.pushScissors(scissors)) {
        return false;
      }
    }
    return true;
  }
  
  public Rectangle getVisibleArea() {
    Rectangle visibleArea = ScissorStack.getViewport();
    float cupp = getCupp();
    visibleArea.x = visibleArea.x * cupp - (screenWidth * cupp - canvasWidth) * 0.5f;
    visibleArea.y = visibleArea.y * cupp - (screenHeight * cupp - canvasHeight) * 0.5f;
    visibleArea.width *= cupp;
    visibleArea.height *= cupp;
    return visibleArea;
  }
  
  private void beginShaderBuffer(GdxShader shader) {
//    shaderArea.set(shader.getArea());
    shaderFrameBuffer = prepareFrameBuffer(shaderFrameBuffer, shaderArea.width, shaderArea.height);
    beginBuffer(shaderFrameBuffer);
//    setTransformMatrix(getTransformMatrix().setToTranslation(- shaderArea.x, - shaderArea.y, 0));
    moveClippingAreas(- shaderArea.x, - shaderArea.y);
    recalculateClippingAreas();
//    Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }
  
  private void swapShaderBuffer() {
    endBuffer();
    /* Swap shader framebuffer for other framebuffer */
    ExtendedFrameBuffer previousFrameBuffer = shaderFrameBuffer;
    shaderFrameBuffer = prepareFrameBuffer(otherFrameBuffer, shaderArea.width, shaderArea.width);
    otherFrameBuffer = previousFrameBuffer;
    beginBuffer(shaderFrameBuffer);
    /* Draws content of previous framebuffer into new buffer */
//    setTransformMatrix(getTransformMatrix().setToTranslation(0, 0, 0));
    Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    previousFrameBuffer.drawTexture(0, 0);
//    draw(previousFrameBuffer.getColorBufferTexture(), 
//        0, 0, shaderArea.width, shaderArea.height,
//        0, 0, (int)(shaderArea.width * getPpcu()), (int)(shaderArea.height * getPpcu()),
//        false, false);
  }
  
  private void flushShaderBuffer() {
    endBuffer();
//    setTransformMatrix(getTransformMatrix().setToTranslation(0, 0, 0));
    if (!scissorStackEmpty) Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
    moveClippingAreas(shaderArea.x, shaderArea.y);
    recalculateClippingAreas();
    shaderFrameBuffer.drawTexture(shaderArea.x, shaderArea.y);    
//    draw(shaderFrameBuffer.getColorBufferTexture(), 
//        shaderArea.x, shaderArea.y, shaderArea.width, shaderArea.height, 
//        0, 0, (int)(shaderArea.width * getPpcu()), (int)(shaderArea.height * getPpcu()),
//        false, false);
//    shaderArea.set(0, 0, 0, 0);
  }
  
  private void beginShaders() {
    if (!currentShaders.isEmpty()) {
      GdxShader shader = currentShaders.get(currentShaders.size() - 1);
      if (currentShaders.size() > 1) beginShaderBuffer(shader);
      spriteBatch.setShader(shader.getProgram());
      if (currentShaders.size() > 1) {
        shader.prepare(this, shaderArea.width, shaderArea.height);
      } else {
        shader.prepare(this, canvasWidth, canvasHeight);
      }
    }
  }
  
  private void endShaders() {
    int shaderCount = currentShaders.size();
    if (shaderCount > 1) {
      //System.out.println("shader count = " + shaderCount);
      for (int i = shaderCount - 2; i > 0; i--) {
        GdxShader shader = currentShaders.get(i);
        spriteBatch.setShader(shader.getProgram());
        shader.prepare(this, shaderArea.width, shaderArea.height);
        //System.out.println("shader area = " + shaderArea);
        swapShaderBuffer();
      }
      GdxShader shader = currentShaders.get(0);
      spriteBatch.setShader(shader.getProgram());
      shader.prepare(this, canvasWidth, canvasHeight);
      //System.out.println("shader area = " + shaderArea);
      flushShaderBuffer();
    }
    spriteBatch.setShader(null);
  }
  
  @Override
  public void pushShader(GdxShader shader) {
    endShaders();
//    shaderArea.set(shader.getArea());
    currentShaders.add(shader);
    beginShaders();
  }
  
  @Override
  public GdxShader popShader() {
    endShaders();
    int index = currentShaders.size() - 1;
    GdxShader shader = currentShaders.remove(index--);
//    if (index >= 0)
//      shaderArea.set(currentShaders.get(index).getArea());
//    else
//      shaderArea.set(0, 0, screenWidth, screenHeight);
    beginShaders();
    return shader;
  }
  
  @Override
  public void paintComponent(float x, float y, GdxComponent component) {
    if (component.isVisible()) {
//      boolean hasShaderEffect = false;
      for (GdxVisualEffect visualEffect : component.getVisualEffects()) {
        if (visualEffect instanceof GdxShader) {
          if (currentShaders.size() > 0) {
            shaderArea.set(((GdxShader)visualEffect).getArea());
          }
//          if (!hasShaderEffect) {
//            endShaders();
//            hasShaderEffect = true;
//          }
        }
        currentVisualEffects.add(visualEffect);
        visualEffect.before(x - shaderArea.x, y - shaderArea.y, this);
      }
//      if (hasShaderEffect) beginShaders();
      component.paint(x - shaderArea.x, y - shaderArea.y, this);
//      if (hasShaderEffect) endShaders();
      int index = currentVisualEffects.size() - 1;
      for (int effectCount = component.getVisualEffects().size(); effectCount > 0; effectCount--) {
        GdxVisualEffect visualEffect = currentVisualEffects.remove(index--);
        visualEffect.after(x - shaderArea.x, y - shaderArea.y, this);
        if (currentShaders.size() < 2) {
          shaderArea.set(0, 0, screenWidth, screenHeight);
        }
      }
//      if (hasShaderEffect) beginShaders();
    }
  }
  
  @Override
  public void draw(Texture texture, float x, float y) {
    spriteBatch.draw(texture, x, y);
  }

  @Override
  public void draw(Texture texture, float x, float y, float width, float height) {
    spriteBatch.draw(texture, x, y, width, height);
  }

  @Override
  public void draw(Texture texture, float x, float y, int srcX, int srcY, 
      int srcWidth, int srcHeight) {
    spriteBatch.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
  }

  @Override
  public void draw(Texture texture, float x, float y, float width, float height, 
      int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
    spriteBatch.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, 
        flipX, flipY);
  }

  @Override
  public void draw(Texture texture, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation, 
      int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
    spriteBatch.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, 
        rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
  }

  @Override
  public void draw(Texture texture, float x, float y, float width, float height, 
      float u, float v, float u2, float v2) {
    spriteBatch.draw(texture, x, y, width, height, u, v, u2, v2);
  }

  @Override
  public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
    spriteBatch.draw(texture, spriteVertices, offset, count);
  }

  @Override
  public void draw(TextureRegion region, float x, float y) {
    spriteBatch.draw(region, x, y);
  }

  @Override
  public void draw(TextureRegion region, float x, float y, float width, float height) {
    spriteBatch.draw(region, x, y, width, height);
  }

  @Override
  public void draw(TextureRegion region, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation) {
    spriteBatch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, 
        rotation);
  }

  @Override
  public void draw(TextureRegion region, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation, 
      boolean clockwise) {
    spriteBatch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, 
        rotation, clockwise);
  }

  @Override
  public void draw(TextureRegion region, float width, float height, Affine2 transform) {
    spriteBatch.draw(region, width, height, transform);
  }
  
  @Override
  public float measureTextWidth(BitmapFont font, String text) {
    glyphLayout.setText(font, text);
    return glyphLayout.width;
  }
  
  @Override
  public float measureTextHeight(BitmapFont font, String text) {
    glyphLayout.setText(font, text);
    return glyphLayout.height;
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, float x, float y) {
    font.draw(spriteBatch, text, x, y);
  }

  @Override
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity) {
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        x + (width - glyphLayout.width) * horizontalGravity, 
        y + (height - glyphLayout.height) * verticalGravity);
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity) {
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        area.x + (area.width - glyphLayout.width) * horizontalGravity, 
        area.y + (area.height - glyphLayout.height) * verticalGravity);
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale) {
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        x + (width - glyphLayout.width) * horizontalGravity, 
        y + (height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, float scale) {
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        area.x + (area.width - glyphLayout.width) * horizontalGravity, 
        area.y + (area.height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale, float alpha) {
    Color color = font.getColor();
    float savedAlpha = color.a;
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    color.a *= alpha;
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        x + (width - glyphLayout.width) * horizontalGravity, 
        y + (height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
    color.a = savedAlpha;
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, 
      float scale, float alpha) {
    Color color = font.getColor();
    float savedAlpha = color.a;
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    color.a *= alpha;
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        area.x + (area.width - glyphLayout.width) * horizontalGravity, 
        area.y + (area.height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
    color.a = savedAlpha;
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale, Color color) {
    Color savedColor = new Color(font.getColor());
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    font.setColor(color);
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        x + (width - glyphLayout.width) * horizontalGravity, 
        y + (height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
    font.setColor(savedColor);
  }
  
  @Override
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, 
      float scale, Color color) {
    Color savedColor = new Color(font.getColor());
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    font.setColor(color);
    font.getData().setScale(scaleX * scale, scaleY * scale);
    glyphLayout.setText(font, text);
    font.draw(spriteBatch, text, 
        area.x + (area.width - glyphLayout.width) * horizontalGravity, 
        area.y + (area.height - glyphLayout.height) * verticalGravity);
    font.getData().setScale(scaleX, scaleY);
    font.setColor(savedColor);
  }
  
  /**
   * Convenience method. Draws a simple uniformly colored rectangle.
   * If not called in shape batch, it automatically starts and ends one.
   * @param x Leftmost point of the rectangle
   * @param y Topmost point of the rectangle
   * @param width Width of the rectangle
   * @param height Height of the rectangle
   * @param color Color of the rectangle
   */
  public void fillRect(float x, float y, float width, float height, Color color) {
    boolean doStartShapeBatch = shapeRenderer == null || !shapeRenderer.isDrawing();
    ShapeType savedShapeType = null;
    if (doStartShapeBatch) {
      beginShapeBatch(ShapeType.Filled);
    } else {
      savedShapeType = shapeRenderer.getCurrentType();
      shapeRenderer.set(ShapeType.Filled);
    }
    Color savedColor = shapeRenderer.getColor();
    shapeRenderer.setColor(color);
    shapeRenderer.rect(x, y, width, height);
    shapeRenderer.setColor(savedColor);
    if (doStartShapeBatch) {
      endShapeBatch();
    } else {
      shapeRenderer.set(savedShapeType);
    }
  }
  
  /**
   * Draws a rectangle filled with simple or complex linear gradient.
   * If not called in shape batch, it automatically starts and ends one.
   * @param x Leftmost point of the rectangle
   * @param y Topmost point of the rectangle
   * @param width Width of the rectangle
   * @param height Height of the rectangle
   * @param gradientColors Color of the gradient from left to right (at least two elements)
   * @param gradientPositions Positions of the color stops within gradient as a percentage values
   *                          ranging from 0 to 1 (two less elements than number of colors)
   * @param gradientAngle Direction of a gradient in degrees, starting from zero for left to right
   *                      and turning counter clockwise
   */
  public void fillRect(float x, float y, float width, float height, Color[] gradientColors, float[] gradientPositions, float gradientAngle) {
    if (!pushClippingArea(x, y, width, height)) return;
    boolean doStartShapeBatch = shapeRenderer == null || !shapeRenderer.isDrawing();
    ShapeType savedShapeType = null;
    if (doStartShapeBatch) {
      beginShapeBatch(ShapeType.Filled);
    } else {
      savedShapeType = shapeRenderer.getCurrentType();
      shapeRenderer.set(ShapeType.Filled);
    }
    if (gradientAngle == 0) {
      float segmentX = 0;
      for (int i = 0; i < gradientColors.length - 1; i++) {
        boolean lastSegment = i == gradientColors.length - 2;
        float segmentWidth = lastSegment ? (width - segmentX) : (gradientPositions[i] * width);
        shapeRenderer.rect(x + segmentX, y, segmentWidth, height, gradientColors[i], gradientColors[i + 1], gradientColors[i + 1], gradientColors[i]);
        segmentX += segmentWidth;
      }
    } else if (gradientAngle == 90) {
      float segmentY = 0;
      for (int i = gradientColors.length - 1; i > 0; i--) {
        boolean lastSegment = i == 1;
        float segmentHeight = lastSegment ? (height - segmentY) : (gradientPositions[i - 2] * height);
        shapeRenderer.rect(x, y + segmentY, width, segmentHeight, gradientColors[i], gradientColors[i], gradientColors[i - 1], gradientColors[i - 1]);
        segmentY += segmentHeight;
      }
    } else if (gradientAngle == 180) {
      float segmentX = 0;
      for (int i = gradientColors.length - 1; i > 0; i--) {
        boolean lastSegment = i == 1;
        float segmentWidth = lastSegment ? (width - segmentX) : (gradientPositions[i - 2] * width);
        shapeRenderer.rect(x + segmentX, y, segmentWidth, height, gradientColors[i], gradientColors[i - 1], gradientColors[i - 1], gradientColors[i]);
        segmentX += segmentWidth;
      }
    } else if (gradientAngle == 270) {
      float segmentY = 0;
      for (int i = 0; i < gradientColors.length - 1; i++) {
        boolean lastSegment = i == gradientColors.length - 2;
        float segmentHeight = lastSegment ? (height - segmentY) : (gradientPositions[i] * height);
        shapeRenderer.rect(x, y + segmentY, width, segmentHeight, gradientColors[i], gradientColors[i], gradientColors[i + 1], gradientColors[i + 1]);
        segmentY += segmentHeight;
      }
    } else {
      double radAngle = gradientAngle / 180 * Math.PI;
      float angleAbsSin = (float)Math.abs(Math.sin(radAngle));
      float angleAbsCos = (float)Math.abs(Math.cos(radAngle));
      float boundBoxWidth = angleAbsCos * width + angleAbsSin * height;
      float boundBoxHeight = angleAbsSin * width + angleAbsCos * height;
      x -= (boundBoxWidth - width) * 0.5f;
      y -= (boundBoxHeight - height) * 0.5f;
      float segmentX = 0;
      for (int i = 0; i < gradientColors.length - 1; i++) {
        boolean lastSegment = i == gradientColors.length - 2;
        float segmentWidth = lastSegment ? boundBoxWidth - segmentX : (gradientPositions[i] * boundBoxWidth);
        shapeRenderer.rect(x + segmentX, y, boundBoxWidth * 0.5f - segmentX, boundBoxHeight * 0.5f, segmentWidth, boundBoxHeight, 
            1, 1, - gradientAngle, gradientColors[i], gradientColors[i + 1], gradientColors[i + 1], gradientColors[i]);
        segmentX += segmentWidth;
      }
    }
    if (doStartShapeBatch) {
      endShapeBatch();
      popClippingArea();
    } else {
      shapeRenderer.end();
      popClippingArea();
      shapeRenderer.begin(savedShapeType);
    }
  }
  
  /**
   * Returns all textures attached to texture framebuffers in a given array.
   * If NULL pointer is provided instead or the length of the array does
   * not match the number of the textures, a new array is created instead.
   * <p>
   * This method is meant for debugging purposes only.
   * @param textureArray Array to store the results or NULL.
   * @return Texture attached to the selected framebuffer or NULL.
   */
  public Texture[] getTextureFrameBufferTextures(Texture[] textureArray) {
    int textureCount = textureFrameBuffers.size();
    if (textureArray == null || textureArray.length != textureCount) {
      textureArray = new Texture[textureCount];
    }
    int index = 0;
    for (ExtendedFrameBuffer frameBuffer : textureFrameBuffers.values()) {
      textureArray[index++] = frameBuffer.getColorBufferTexture();
    }
    return textureArray;
  }
  
  /**
   * Returns an entire texture attached to a shader framebuffer. 
   * If the framebuffer is currently not initialized, null pointer is returned instead.
   * <p>
   * This method is meant for debugging purposes only.
   * @return Texture attached to the framebuffer or NULL.
   */
  public Texture getShaderFrameBufferTexture() {
    if (shaderFrameBuffer == null) return null;
    return shaderFrameBuffer.getColorBufferTexture();
  }

  /**
   * Returns an entire texture attached to a other framebuffer. 
   * If the framebuffer is currently not initialized, null pointer is returned instead.
   * <p>
   * This method is meant for debugging purposes only.
   * @return Texture attached to the framebuffer or NULL.
   */
  public Texture getOtherFrameBufferTexture() {
    if (otherFrameBuffer == null) return null;
    return otherFrameBuffer.getColorBufferTexture();
  }
  
  @Override
  public void dispose() {
    for (ExtendedFrameBuffer frameBuffer : textureFrameBuffers.values()) {
      frameBuffer.dispose();
    }
    textureFrameBuffers.clear();
    if (shaderFrameBuffer != null) shaderFrameBuffer.dispose();
    shaderFrameBuffer = null;
    if (otherFrameBuffer != null) otherFrameBuffer.dispose();
    otherFrameBuffer = null;
    if (shapeRenderer != null) shapeRenderer.dispose();
    shapeRenderer = null;
    if (premultiplyShader != null) premultiplyShader.dispose();
    premultiplyShader = null;
    if (demultiplyShader != null) demultiplyShader.dispose();
    demultiplyShader = null;
    spriteBatch.dispose();
  }
  
  private static class TextureKey {
    public final GdxComponent renderer;
    public final int id;
    private final int hashCode;
    
    public TextureKey(GdxComponent renderer, int id) {
      this.renderer = renderer;
      this.id = id;
      hashCode = calculateHashCode();
    }

    private int calculateHashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((renderer == null) ? 0 : renderer.hashCode());
      return result;
    }
    
    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null) return false;
      if (getClass() != object.getClass()) return false;
      TextureKey other = (TextureKey)object;
      if (id != other.id) return false;
      if (renderer == null) {
        if (other.renderer != null) return false;
      } else if (!renderer.equals(other.renderer)) {
        return false;
      }
      return true;
    }
  }
  
  private static class SavedFrameBuffer {
    public final ExtendedFrameBuffer frameBuffer;
    public final Camera camera;
    
    public SavedFrameBuffer(ExtendedFrameBuffer frameBuffer, Camera camera) {
      this.frameBuffer = frameBuffer;
      this.camera = camera;
    }
  }
  
  protected class ExtendedFrameBuffer extends FrameBuffer {
    private final float canvasWidth, canvasHeight;
    private int textureWidth, textureHeight;
    private TextureRegion textureRegion;
    
    public ExtendedFrameBuffer(Format format, float width, float height, boolean hasDepth, boolean hasStencil) {
      super(format, (int)(width * getPpcu()), (int)(height * getPpcu()), hasDepth, hasStencil);
      canvasWidth = width;
      canvasHeight = height;
      textureWidth = getWidth();
      textureHeight = getHeight();
      textureRegion = new TextureRegion(getColorBufferTexture());
    }

    public ExtendedFrameBuffer(Format format, float width, float height, boolean hasDepth) {
      super(format, (int)(width * getPpcu()), (int)(height * getPpcu()), hasDepth);
      canvasWidth = width;
      canvasHeight = height;
      textureWidth = getWidth();
      textureHeight = getHeight();
      textureRegion = new TextureRegion(getColorBufferTexture());
    }
    
    public void setTextureSize(float width, float height) {
      textureWidth = (int)(width * getPpcu());
      textureHeight = (int)(height * getPpcu());
    }
    
    public float getCanvasWidth() {
      return canvasWidth;
    }

    public float getCanvasHeight() {
      return canvasHeight;
    }

    public void drawTexture(float x, float y) {
      draw(getColorBufferTexture(), 
          x, y, textureWidth * getCupp(), textureHeight * getCupp(), 
          0, getHeight() - textureHeight, textureWidth, textureHeight,
          false, false);
    }
    
    public TextureRegion getTexture() {
      textureRegion.setRegion(0, getHeight() - textureHeight, textureWidth, textureHeight);
      return textureRegion;
    }
  }
  
  protected class ExtendedShapeRenderer extends ShapeRenderer {
    private boolean premultiplyAlpha = false;
    private Color originalColor;

    @Override
    public void begin() {
      spriteBatch.end();
      Gdx.gl.glEnable(GL20.GL_BLEND);
      setPremultiplyAlpha(spriteBatch.getShader() == getPremultiplyShader());
      super.begin();
      setProjectionMatrix(currentCamera.combined);
      setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void begin(ShapeType shapeType) {
      spriteBatch.end();
      Gdx.gl.glEnable(GL20.GL_BLEND);
      setPremultiplyAlpha(spriteBatch.getShader() == getPremultiplyShader());
      super.begin(shapeType);
      setProjectionMatrix(currentCamera.combined);
      setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void end() {
      super.end();
      Gdx.gl.glDisable(GL20.GL_BLEND);
      spriteBatch.begin();
    }

    public void setPremultiplyAlpha(boolean premultiplyAlpha) {
      if (!this.premultiplyAlpha && premultiplyAlpha) {
        originalColor = super.getColor();
        super.setColor(
            originalColor.r * originalColor.a, 
            originalColor.g * originalColor.a, 
            originalColor.b * originalColor.a, 
            originalColor.a);
      } else if (!premultiplyAlpha && originalColor != null) {
        super.setColor(originalColor);
        originalColor = null;
      }
      this.premultiplyAlpha = premultiplyAlpha;
    }

    @Override
    public Color getColor() {
      if (premultiplyAlpha) {
        return originalColor;
      } else {
        return super.getColor();
      }
    }

    @Override
    public void setColor(Color color) {
      if (premultiplyAlpha) {
        originalColor = color;
        color = new Color(color.r * color.a, color.g * color.a, color.b * color.a, color.a);
      }
      super.setColor(color);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
      if (premultiplyAlpha) {
        originalColor = new Color(r, g, b, a);
        r *= a; g *= a; b *= a;
      }
      super.setColor(r, g, b, a);
    }

    @Override
    public void line(float x, float y, float z, float x2, float y2, float z2, Color c1, Color c2) {
      if (premultiplyAlpha) {
        c1 = new Color(c1.r * c1.a, c1.g * c1.a, c1.b * c1.a, c1.a);
        c2 = new Color(c2.r * c2.a, c2.g * c2.a, c2.b * c2.a, c2.a);
      }
      super.line(x, y, z, x2, y2, z2, c1, c2);
    }

    @Override
    public void rect(float x, float y, float width, float height, Color col1, Color col2, Color col3, Color col4) {
      if (premultiplyAlpha) {
        col1 = new Color(col1.r * col1.a, col1.g * col1.a, col1.b * col1.a, col1.a);
        col2 = new Color(col2.r * col2.a, col2.g * col2.a, col2.b * col2.a, col2.a);
        col3 = new Color(col3.r * col3.a, col3.g * col3.a, col3.b * col3.a, col3.a);
        col4 = new Color(col4.r * col4.a, col4.g * col4.a, col4.b * col4.a, col4.a);
      }
      super.rect(x, y, width, height, col1, col2, col3, col4);
    }

    @Override
    public void rect(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, 
        float degrees, Color col1, Color col2, Color col3, Color col4) {
      if (premultiplyAlpha) {
        col1 = new Color(col1.r * col1.a, col1.g * col1.a, col1.b * col1.a, col1.a);
        col2 = new Color(col2.r * col2.a, col2.g * col2.a, col2.b * col2.a, col2.a);
        col3 = new Color(col3.r * col3.a, col3.g * col3.a, col3.b * col3.a, col3.a);
        col4 = new Color(col4.r * col4.a, col4.g * col4.a, col4.b * col4.a, col4.a);
      }
      super.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees, col1, col2, col3, col4);
    }

    @Override
    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, Color col1, Color col2, Color col3) {
      if (premultiplyAlpha) {
        col1 = new Color(col1.r * col1.a, col1.g * col1.a, col1.b * col1.a, col1.a);
        col2 = new Color(col2.r * col2.a, col2.g * col2.a, col2.b * col2.a, col2.a);
        col3 = new Color(col3.r * col3.a, col3.g * col3.a, col3.b * col3.a, col3.a);
      }
      super.triangle(x1, y1, x2, y2, x3, y3, col1, col2, col3);
    }
  }
  
  private static final String FRAGMENT_PREMULTIPLY =
      "precision lowp float;\n" +

      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +

      "void main() {\n" +
      "  vec4 color = texture2D(u_texture, v_texCoord0) * v_color;\n" +
      "  gl_FragColor = vec4(color.r, color.g, color.b, 1.0) * color.a;\n" +
      "}\n";
  private static final String FRAGMENT_DEMULTIPLY =
      "precision lowp float;\n" +

      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +

      "void main() {\n" +
      "  vec4 color = texture2D(u_texture, v_texCoord0);\n" +
      "  gl_FragColor = vec4(color.rgb / color.a, color.a);\n" +
      "}\n";
}
