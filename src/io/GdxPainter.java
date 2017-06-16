package io;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.Ray;
import components.interfaces.GdxComponent;

public interface GdxPainter {
  public static final String TAG = GdxPainter.class.getSimpleName();

  public static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

  public void begin();

  public void end();
  
  public void flush();

  /**
   * Allows to substitute an entirely custom camera for rendering.
   * @param specialCamera Camera wrapping class
   */
  public void setSpecialCamera(GdxSpecialCamera specialCamera);

  public GdxSpecialCamera getSpecialCamera();

  /**
   * Sets a size of canvas in internal units.
   * @param width Internal width
   * @param height Internal height
   */
  public void setSize(float width, float height);
  
  public float getWidth();
  
  public float getHeight();

  /**
   * Sets a size of screen in pixels (i.e. actual screen resolution).
   * @param screenWidth Width of screen in pixels
   * @param screenHeight Height of screen in pixels
   */
  public void setScreenSize(int screenWidth, int screenHeight);

  public int getScreenWidth();

  public int getScreenHeight();
  
  public int getOriginLeft();
  
  public int getOriginTop();

  public Ray getPickingRay(int screenX, int screenY);

  /**
   * @return Number of internal units represented by one pixel on screen
   * @deprecated Use {@link GdxPainter#getCanvasUnitsPerPixel()} or {@link GdxPainter#getCupp()} instead.
   */
  public float getScale();

  public float getCanvasUnitsPerPixel();

  /**
   * Alias for {@link GdxPainter#getCanvasUnitsPerPixel()}.
   * @return Number of internal units represented by one pixel on screen
   */
  public float getCupp();

  public float getPixelsPerCanvasUnit();

  /**
   * Alias for {@link GdxPainter#getPixelsPerCanvasUnit()}.
   * @return Number of pixels representing a single internal unit on screen
   */
  public float getPpcu();
  
  public void enableBlending();
  
  public void disableBlending();
  
  public void setColor(Color tint);
  
  public void setColor(float color);
  
  public void setColor(float r, float g, float b, float a); 
  
  public Color getColor();

  public void setClearColor(Color clearColor);

  public Color getClearColor();

  public void setBlackBorders(boolean blackBorders);

  public boolean isBlackBorders();

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
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @param clearColor Color used to clear a buffer before rendering (can be transparent).
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height, Color clearColor);
  
  /**
   * Variant of io.GdxPainter#requestTextureRender(GdxComponent, int, float, float, Color)
   * which always uses a transparent color to clear a canvas.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param width Width in internal units.
   * @param height Height in internal units.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, float width, float height);

  /**
   * Variant of io.GdxPainter#requestTextureRender(GdxComponent, int, float, float, Color)
   * which automatically initializes its size to size of the component responsible for rendering.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @param clearColor Color used to clear a buffer before rendering (can be transparent).
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id, Color clearColor);

  /**
   * Variant of io.GdxPainter#requestTextureRender(GdxComponent, int, float, float, Color)
   * which automatically initializes its size to size of the component responsible for rendering 
   * and always uses a transparent color to clear a canvas.
   * @param component GdxComponent Component responsible for rendering.
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to perform multiple renderings.
   * @return Texture region covering a rendered texture.
   */
  public TextureRegion requestTextureRender(GdxComponent component, int id);

  public boolean pushClippingArea(float x, float y, float width, float height);

  public boolean pushClippingArea(Rectangle area);

  public Rectangle popClippingArea();

  public void pushShader(GdxShader shader);

  public GdxShader popShader();

  public void paintComponent(float x, float y, GdxComponent component);
  
  public void draw(Texture texture, float x, float y);
  
  public void draw(Texture texture, float x, float y, float width, float height);
  
  public void draw(Texture texture, float x, float y, int srcX, int srcY, 
      int srcWidth, int srcHeight);
  
  public void draw(Texture texture, float x, float y, float width, float height, 
      int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY);
  
  public void draw(Texture texture, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation, 
      int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY);
  
  public void draw(Texture texture, float x, float y, float width, float height, 
      float u, float v, float u2, float v2);

  public void draw(Texture texture, float[] spriteVertices, int offset, int count);
  
  public void draw(TextureRegion region, float x, float y);
  
  public void draw(TextureRegion region, float x, float y, float width, float height);
  
  public void draw(TextureRegion region, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation);
  
  public void draw(TextureRegion region, float x, float y, float originX, float originY, 
      float width, float height, float scaleX, float scaleY, float rotation, 
      boolean clockwise);
  
  public void draw(TextureRegion region, float width, float height, Affine2 transform);
  
  /**
   * Convenience method. Measures a width of a given text using a given font.
   * @param font Font to be used
   * @param text Text to be measured
   * @return Width of the text written in a single line
   */
  public float measureTextWidth(BitmapFont font, String text);

  /**
   * Convenience method. Measures a height of a given text using a given font.
   * @param font Font to be used
   * @param text Text to be measured
   * @return Height of the text written in a single line
   */
  public float measureTextHeight(BitmapFont font, String text);

  /**
   * Draws a single line of text at a given coordinates.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param x Leftmost point of drawing area
   * @param y Topmost point of drawing area
   */
  public void drawText(BitmapFont font, CharSequence text, float x, float y);
  
  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param x Left boundary of the area
   * @param y Top boundary of the area
   * @param width Width of the area
   * @param height Height of the area
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   */
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param area Area to draw in
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   */
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param x Left boundary of the area
   * @param y Top boundary of the area
   * @param width Width of the area
   * @param height Height of the area
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param area Area to draw in
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, float scale);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param x Left boundary of the area
   * @param y Top boundary of the area
   * @param width Width of the area
   * @param height Height of the area
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   * @param alpha Alpha to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale, float alpha);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param area Area to draw in
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   * @param alpha Alpha to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, 
      float scale, float alpha);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param x Left boundary of the area
   * @param y Top boundary of the area
   * @param width Width of the area
   * @param height Height of the area
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   * @param color Color to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, float x, float y, float width, float height, 
      float horizontalGravity, float verticalGravity, float scale, Color color);

  /**
   * Convenience method. Draws a single line of text within a given area.
   * Text is aligned according to horizontal and vertical gravity, intervals
   * <0, 1> ranging between left and right alignment or top and bottom alignment.
   * Text may overflow from the area.
   * @param font Font to be used
   * @param text Text to be drawn
   * @param area Area to draw in
   * @param horizontalGravity Ranges from 0 for left alignment and 1 for right alignment
   * @param verticalGravity Ranges from 0 for top alignment and 1 for bottom alignment
   * @param scale Scale to be applied on font for this drawing
   * @param color Color to be applied on font for this drawing
   */
  public void drawText(BitmapFont font, CharSequence text, Rectangle area, float horizontalGravity, float verticalGravity, 
      float scale, Color color);
  
  public void dispose();
}