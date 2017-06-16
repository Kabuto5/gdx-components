package components.abstracts;

import effects.GdxAbstractVisualEffect;
import general.GdxModelInstance;
import helpers.ModelUtils;
import helpers.collections.SingleElementCollection;
import io.GdxPainter;
import io.GdxPainter3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import components.bounds.GdxBoundingBox;
import components.bounds.GdxBoundingShape;
import components.interfaces.GdxComponent3D;
import components.interfaces.GdxContainer;

public abstract class GdxAbstractComponent3D extends GdxAbstractComponent implements GdxComponent3D, RenderableProvider {
  public static final Vector3 AXIS_X = new Vector3(1, 0, 0);
  public static final Vector3 AXIS_Y = new Vector3(0, 1, 0);
  public static final Vector3 AXIS_Z = new Vector3(0, 0, 1);
  
  private static ModelBuilder modelBuilderInstance;
  
  private float z;
  private float depth;
  private Collection<GdxModelInstance> models;
  private Collection<GdxBoundingShape> bounds;
  private boolean boundsInvalid = true;
  private Vector3 lastLocation;
  private Vector3 lastSize;
  
  public GdxAbstractComponent3D(Collection<GdxModelInstance> models, float x, float y, float z) {
    super(x, y, ModelUtils.calculateBoundingBoxWidth(models), ModelUtils.calculateBoundingBoxHeight(models));
    this.z = z;
    this.depth = ModelUtils.calculateBoundingBoxDepth(models);
    this.models = models;
    for (GdxModelInstance model : models) {
      model.translate(x, y, z);
    }
    lastLocation = new Vector3(x, y, z);
    lastSize = new Vector3(getWidth(), getHeight(), depth);
    this.bounds = initializeBounds(models);
  }
  
  public GdxAbstractComponent3D(float x, float y, float z, GdxModelInstance... models) {
    this(Arrays.asList(models), x, y, z);
  }
  
  public GdxAbstractComponent3D(GdxModelInstance model, float x, float y, float z) {
    this(SingleElementCollection.create(model), x, y, z);
  }
  
  protected static ModelBuilder getModelBuilder() {
    if (modelBuilderInstance == null) modelBuilderInstance = new ModelBuilder();
    return modelBuilderInstance;
  }
  
  public final float getZ() {
    return z;
  }

  public final void setZ(float z) {
    this.z = z;
    moved();
  }

  public Vector3 getLocation(Vector3 out) {
    return out.set(getX(), getY(), z);
  }
  
  public void setLocation(float x, float y, float z) {
    this.z = z;
    setLocation(x, y);
  }

  public void setLocation(Vector3 location) {
    this.z = location.z;
    setLocation(location.x, location.y);
  }
  
  public float getDepth() {
    return depth;
  }

  public void setDepth(float depth) {
    this.depth = depth;
    resized();
  }

  public Vector3 getSize(Vector3 out) {
    return out.set(getWidth(), getHeight(), depth);
  }

  public void setSize(float width, float height, float depth) {
    this.depth = depth;
    setSize(width, height);
  }

  public void setSize(Vector3 size) {
    this.depth = size.z;
    setSize(size.x, size.y);
  }

  protected void resized() {
    super.resized();
    float width = getWidth();
    float height = getHeight();
    scaleModels(width / lastSize.x, height / lastSize.y, depth / lastSize.z);
    lastSize.set(width, height, depth);
  }
  
  protected void moved() {
    super.moved();
    float x = getX();
    float y = getY();
    translateModels(x - lastLocation.x, y - lastLocation.y, z - lastLocation.z);
    lastLocation.set(x, y, z);
  }
  
  public void reportMove(GdxContainer container) {
    super.reportMove(container);
    float x = getX();
    float y = getY();
    translateModels(x - lastLocation.x, y - lastLocation.y, z - lastLocation.z);
    lastLocation.set(x, y, z);
  }
  
  @Override
  public void setInteractiveAreaExtension(float size) {
    super.setInteractiveAreaExtension(size);
    invalidateBounds();
  }
  
  protected Collection<GdxModelInstance> getModels() {
    return models;
  }
  
  protected Collection<GdxBoundingShape> getBounds() {
    return bounds;
  }

  protected void translateModels(float x, float y, float z) {
    if (x == 0 && y == 0 && z == 0) return;
    for (GdxModelInstance model : models) {
      model.translate(x, y, z);
    }
    for (GdxBoundingShape shape : bounds) {
      shape.translate(x, y, z);
    }
  }

  protected void scaleModels(float scaleX, float scaleY, float scaleZ) {
    if (scaleX == 1 && scaleY == 1 && scaleZ == 1) return;
    for (GdxModelInstance model : models) {
      model.scale(scaleX, scaleY, scaleZ);
    }
    for (GdxBoundingShape shape : bounds) {
      shape.scale(scaleX, scaleY, scaleZ);
    }
  }
  
  protected void rotateModels(Vector3 axis, float angle) {
    float pivotX = getFrameX() + getWidth() / 2;
    float pivotY = getFrameY() + getHeight() / 2;
    float pivotZ = z + depth / 2;
    for (GdxModelInstance model : models) {
      model.pivotRotate(pivotX, pivotY, pivotZ, axis.x, axis.y, axis.z, angle);
    }
    invalidateBounds();
  }
  
  /**
   * Causes component bounds to be updated when necessary.
   * <p>
   * This method should be called whenever component bounds are to be changed, rather than calling
   * {@link #updateBounds(Collection, Collection) updateBounds} method directly, for performance reasons.
   */
  protected void invalidateBounds() {
    boundsInvalid = true;
  }

  /**
   * Initializes the component bounds.
   * @param models Model instances the component bounds are based on.
   * @return Collection of shapes the component bounds consists of.
   */
  protected Collection<GdxBoundingShape> initializeBounds(Collection<GdxModelInstance> models) {
    return updateBounds(models, new ArrayList<GdxBoundingShape>(models.size()));
  }
  
  /**
   * Updates the component bounds. Instances of the individual shapes or their total number may change in the process.
   * @param models Model instances the component bounds are based on.
   * @param bounds Collection of shapes the current component bounds consists of.
   * @return Updated collection of shapes the component bounds consists of.
   */
  protected Collection<GdxBoundingShape> updateBounds(Collection<GdxModelInstance> models, Collection<GdxBoundingShape> bounds) {
    Iterator<GdxBoundingShape> shapeIt = bounds.iterator();
    for (GdxModelInstance model : models) {
      GdxBoundingShape shape = null;
      while (shapeIt.hasNext() && !((shape = shapeIt.next()) instanceof GdxBoundingBox)) {
        shapeIt.remove();
      }
      if (shape == null) {
        shape = model.getBoundingBox(new GdxBoundingBox(this));
        bounds.add(shape);
      } else {
        model.getBoundingBox((GdxBoundingBox)shape);
      }
    }
    return bounds;
  }

  @Override
  public boolean intersectRay(Ray pickingRay, Vector3 intersection) {
    if (boundsInvalid) {
      bounds = updateBounds(models, bounds);
      boundsInvalid = false;
    }
    intersection.x = Float.POSITIVE_INFINITY;
    intersection.y = Float.POSITIVE_INFINITY;
    intersection.z = Float.POSITIVE_INFINITY;
    boolean intersects = false;
    for (GdxBoundingShape shape : bounds) {
      intersects = shape.intersectRay(pickingRay, intersection) || intersects;
    }
    return intersects;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    for (GdxModelInstance model : models) {
      model.getRenderables(renderables, pool);
    }
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    for (GdxModelInstance model : models) {
      ((GdxPainter3D)painter).draw(model);
    }
  }
  
  private static final Color DEFAULT_BOUND_COLOR = new Color(1, 1, 0, 0.5f);
  
  /**
   * Adds to the component a visual effect which displays its
   * interactive area in a given color.
   * <p>
   * This method is meant for testing purposes only.
   * @param color Color to be used, or NULL for default
   */
  public void addBoundDisplayer(Color color) {
    if (color == null) color = DEFAULT_BOUND_COLOR;
    addVisualEffect(new BoundDisplayer(color));
  }
  
  private class BoundDisplayer extends GdxAbstractVisualEffect {
    private GdxModelInstance[] boundModels;
    private Color color;
    
    public BoundDisplayer(Color color) {
      super(GdxAbstractComponent3D.this);
      this.color = color;
      updateBoundModels();
    }
    
    private void updateBoundModels() {
      bounds = updateBounds(models, bounds);
      boundsInvalid = false;
      if (boundModels == null || boundModels.length != bounds.size()) {
        boundModels = new GdxModelInstance[bounds.size()];
        ModelBuilder modelBuilder = getModelBuilder();
        int index = 0;
        for (GdxBoundingShape shape : bounds) {
            if (boundModels[index] != null) boundModels[index].getModel().dispose();
            boundModels[index] = shape.createModel(modelBuilder, 
                new Material(ColorAttribute.createDiffuse(color), 
                    new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE)), 
                Usage.Position | Usage.Normal);
          index++;
        }
      }
    }

    @Override
    public void after(float x, float y, GdxPainter painter) {
      super.after(x, y, painter);
      updateBoundModels();
      for (GdxModelInstance modelInstance : boundModels) {
        ((GdxPainter3D)painter).draw(modelInstance);
      }
    }

    @Override
    public void dispose() {
      for (GdxModelInstance modelInstance : boundModels) {
        modelInstance.getModel().dispose();
      }
      super.dispose();
    }
  }
}
