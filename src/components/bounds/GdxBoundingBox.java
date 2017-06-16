package components.bounds;

import general.GdxModelInstance;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import components.interfaces.GdxComponent3D;

public class GdxBoundingBox extends BoundingBox implements GdxBoundingShape {
  private static final long serialVersionUID = -5450203422432530873L;

  private final GdxComponent3D component;

  public GdxBoundingBox(GdxComponent3D component) {
    super();
    this.component = component;
  }

  public GdxBoundingBox(GdxComponent3D component, BoundingBox bounds) {
    super(bounds);
    this.component = component;
  }

  public GdxBoundingBox(GdxComponent3D component, Vector3 minimum, Vector3 maximum) {
    super(minimum, maximum);
    this.component = component;
  }

  public GdxBoundingBox(GdxBoundingBox bounds) {
    super(bounds);
    this.component = bounds.component;
  }
  
  @Override
  public GdxComponent3D getComponent() {
    return component;
  }
  
  @Override
  public int hashCode() {
    return 31 * (31 + min.hashCode()) + max.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (getClass() != object.getClass()) return false;
    GdxBoundingBox other = (GdxBoundingBox)object;
    return min.equals(other.min) && max.equals(other.max);
  }

  public void translate(float x, float y, float z) {
    min.x += x;
    min.y += y;
    min.z += z;
    max.x += x;
    max.y += y;
    max.z += z;
  }
  
  public void scale(float x, float y, float z) {
    float extension = component.getInteractiveAreaExtension();
    max.x = x * (max.x - extension - (min.x + extension)) + min.x + 2 * extension;
    max.y = y * (max.y - extension - (min.y + extension)) + min.y + 2 * extension;
    max.z = z * (max.z - extension - (min.z + extension)) + min.z + 2 * extension;
  }

  public boolean intersectRay(Ray pickingRay, Vector3 intersection) {
    float x = intersection.x;
    float y = intersection.y;
    float z = intersection.z;
    if (Intersector.intersectRayBounds(pickingRay, this, intersection)) {
      if (pickingRay.origin.dst(intersection) > pickingRay.origin.dst(x, y, z)) {
        intersection.x = x;
        intersection.y = y;
        intersection.z = z;
      }
      return true;
    } else {
      intersection.x = x;
      intersection.y = y;
      intersection.z = z;
      return false;
    }
  }

  public GdxModelInstance createModel(ModelBuilder modelBuilder, Material material, long attributes) {
    return new GdxModelInstance(
        modelBuilder.createBox(1, 1, 1, material, attributes), 
        0.5f * (min.x + max.x), 0.5f * (min.y + max.y), 0.5f * (min.z + max.z), 
        max.x - min.x, max.y - min.y, max.z - min.z) {
          @Override
          public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
            setCenter(0.5f * (min.x + max.x), 0.5f * (min.y + max.y), 0.5f * (min.z + max.z));
            setScale(max.x - min.x, max.y - min.y, max.z - min.z);
            super.getRenderables(renderables, pool);
          }
    };
  }
}
