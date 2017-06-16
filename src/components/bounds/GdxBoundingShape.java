package components.bounds;

import general.GdxModelInstance;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import components.interfaces.GdxComponent3D;

public interface GdxBoundingShape {
  public GdxComponent3D getComponent();
  
  public Vector3 getCenter(Vector3 out);
  
  public Vector3 getMin(Vector3 out);
  
  public Vector3 getMax(Vector3 out);
  
  public void translate(float x, float y, float z);
  
  public void scale(float x, float y, float z);
  
  public boolean intersectRay(Ray pickingRay, Vector3 intersection);
  
  /**
   * Builds a model representing this bounding shape. Instance of the model is tied
   * to the bounding shape and automatically updates its attributes according to the
   * current state of the bounding shape before each rendering.
   * <p>
   * The one who requested the model to be created is responsible for its disposal 
   * when it's no longer needed.
   * <p>
   * This method is meant for testing purposes only.
   * @param modelBuilder ModelBuilder to be used to build a model
   * @param material Material of the resulting model
   * @param attributes Attributes of the resulting model
   * @return Model representing the bounding shape
   */
  public GdxModelInstance createModel(ModelBuilder modelBuilder, Material material, long attributes);
}
