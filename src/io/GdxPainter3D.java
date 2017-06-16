package io;

import io.GdxPainter2D.ExtendedFrameBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.Ray;

public class GdxPainter3D extends GdxPainter2D {
  public static final String TAG = GdxPainter3D.class.getSimpleName();
  public static final float FOV_ONE_TO_ONE = 67;
  
  protected final ModelBatch modelBatch = new ModelBatch();
  protected final Environment environment = new Environment();
  private final List<RenderableProvider> renderableProviders = new ArrayList<RenderableProvider>();
  private final PerspectiveCamera mainCamera;
  public Camera currentCamera;
  private GdxSpecialCamera specialCamera;
  private ModelInstance[] axes;
  private ModelInstance highlightedPlane;
  private boolean showAxes = false, highlightPlane = false;
  
  public GdxPainter3D(float canvasWidth, float canvasHeight) {
    super(canvasWidth, canvasHeight);
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
    mainCamera = new PerspectiveCamera();
    mainCamera.fieldOfView = FOV_ONE_TO_ONE * canvasWidth / canvasHeight;
    currentCamera = mainCamera;
    updateCamera3D();
//    super.setSpecialCamera(new GdxSpecialCamera(mainCamera) {
//      @Override
//      public void onResize(float canvasWidth, float canvasHeight,
//          int screenWidth, int screenHeight, float zoom) { }
//    });
    createTestingEntities();
//    System.out.println(Gdx.graphics.getBufferFormat());
  }
  
  @Override
  protected void clearScreen() {
    Color clearColor = getClearColor();
    Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public void end() {
    super.end();
    if (showAxes && axes != null) {
      for (ModelInstance axis : axes) {
        renderableProviders.add(axis);
      }
    }
    if (highlightPlane) {
      renderableProviders.add(highlightedPlane);
    }
    modelBatch.begin(currentCamera);
    modelBatch.render(renderableProviders, environment);
    modelBatch.end();
    renderableProviders.clear();
  }
  
  @Override
  public void flush() {
    modelBatch.flush();
  }
  
  @Override
  public void setSpecialCamera(GdxSpecialCamera specialCamera) {
    this.specialCamera = specialCamera;
    if (specialCamera == null) {
      currentCamera = mainCamera;
    } else {
      currentCamera = specialCamera.getCamera();
      specialCamera.onResize(getWidth(), getHeight(), getScreenWidth(), getScreenHeight(), getCupp());
    }
  }

  @Override
  public GdxSpecialCamera getSpecialCamera() {
    return specialCamera;
  }

  protected void updateCamera3D() {
    float canvasWidth = getWidth();
    float canvasHeight = getHeight();
    int screenWidth = getScreenWidth();
    int screenHeight = getScreenHeight();
    mainCamera.viewportWidth = screenWidth;
    mainCamera.viewportHeight = screenHeight;
    mainCamera.position.x = canvasWidth / 2;
    mainCamera.position.y = - canvasHeight / 2;
    mainCamera.position.z = (float)(canvasHeight / 2 / Math.tan(mainCamera.fieldOfView * 0.5 * Math.PI / 180));
    mainCamera.lookAt(mainCamera.position.x, mainCamera.position.y, 0);
    mainCamera.near = 1f;
    mainCamera.far = 8 * mainCamera.position.z;
    mainCamera.update();
    if (specialCamera != null) {
      specialCamera.onResize(canvasWidth, canvasHeight, screenWidth, screenHeight, getCupp());
    }
  }

  @Override
  public void setSize(float width, float height) {
    super.setSize(width, height);
    updateCamera();
  }

  @Override
  public void setScreenSize(int screenWidth, int screenHeight) {
    super.setScreenSize(screenWidth, screenHeight);
    updateCamera();
  }
  
  public void setShowAxes(boolean showAxes) {
    if (axes == null) createTestingEntities();
    this.showAxes = showAxes;
  }
  
  public void highlightPlane(boolean doHiglight, float position) {
    if (highlightedPlane == null) createTestingEntities();
    highlightedPlane.transform.setToTranslation(50, -50, position);
    highlightedPlane.transform.rotate(1, 0, 0, 180);
    highlightPlane = doHiglight;
  }

  @Override
  public Ray getPickingRay(int screenX, int screenY) {
    Ray pickingRay = currentCamera.getPickRay(screenX, screenY);
    pickingRay.origin.y = - pickingRay.origin.y;
    pickingRay.direction.y = - pickingRay.direction.y;
    return pickingRay;
  }
  
  @Override
  protected ExtendedFrameBuffer createFrameBuffer(float width, float height) {
    return new ExtendedFrameBuffer(Format.RGBA8888, width, height, true);
  }
  
  public void draw(RenderableProvider renderableProvider) {
    renderableProviders.add(renderableProvider);
  }
  
  private void createTestingEntities() {
    ModelBuilder modelBuilder = new ModelBuilder();
    axes = new ModelInstance[3];
    axes[0] = new ModelInstance(modelBuilder.createBox(
        getWidth(), getCupp(), getCupp(), 
        new Material(ColorAttribute.createDiffuse(Color.RED)), 
        Usage.Position | Usage.Normal));
    axes[0].transform.translate(getWidth() / 2, - getHeight() / 2, 0);
    axes[1] = new ModelInstance(modelBuilder.createBox(
        getCupp(), getHeight(), getCupp(), 
        new Material(ColorAttribute.createDiffuse(Color.BLUE)), 
        Usage.Position | Usage.Normal));
    axes[1].transform.translate(getWidth() / 2, - getHeight() / 2, 0);
    axes[2] = new ModelInstance(modelBuilder.createBox(
        getCupp(), getCupp(), Math.min(getWidth(), getHeight()),
        new Material(ColorAttribute.createDiffuse(Color.GREEN)), 
        Usage.Position | Usage.Normal));
    axes[2].transform.translate(getWidth() / 2, - getHeight() / 2, 0);
    highlightedPlane = new ModelInstance(modelBuilder.createRect(
        -50, 50, 0,   50, 50, 0,   50, -50, 0,   -50, -50, 0,   0, 0, 1,
        new Material(
            ColorAttribute.createDiffuse(new Color(1, 1, 0, 0.5f)), 
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        ), Usage.Position | Usage.Normal));
  }
  
  @Override
  public void dispose() {
    modelBatch.dispose();
    for (ModelInstance axis : axes) {
      axis.model.dispose();
    }
    highlightedPlane.model.dispose();
    super.dispose();
  }
}
