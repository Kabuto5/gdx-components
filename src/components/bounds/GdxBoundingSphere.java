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

public class GdxBoundingSphere implements GdxBoundingShape {
  private final GdxComponent3D component;
  public Vector3 center;
  public float radius;
  
  public GdxBoundingSphere(GdxComponent3D component) {
    this.component = component;
    this.center = new Vector3();
    this.radius = 0;
  }
  
  public GdxBoundingSphere(GdxComponent3D component, Vector3 center, float radius) {
    this.component = component;
    this.center = center;
    this.radius = radius;
  }
  
  @Override
  public GdxComponent3D getComponent() {
    return component;
  }
  
  public Vector3 getCenter(Vector3 out) {
    return out.set(center);
  }
  
  public float getCenterX() {
    return center.x;
  }

  public float getCenterY() {
    return center.y;
  }

  public float getCenterZ() {
    return center.z;
  }
  
  public void setCenter(Vector3 center) {
    this.center.set(center);
  }
  
  public void setCenter(float x, float y, float z) {
    this.center.set(x, y, z);
  }
  
  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }
  
  public Vector3 getMin(Vector3 out) {
    float halfDiagonal = (float)(radius * Math.sqrt(3));
    out.x = center.x - halfDiagonal;
    out.y = center.y - halfDiagonal;
    out.z = center.z - halfDiagonal;
    return out;
  }
  
  public Vector3 getMax(Vector3 out) {
    float halfDiagonal = (float)(radius * Math.sqrt(3));
    out.x = center.x + halfDiagonal;
    out.y = center.y + halfDiagonal;
    out.z = center.z + halfDiagonal;
    return out;
  }
  
  @Override
  public int hashCode() {
    return 31 * (31 + Float.floatToIntBits(radius)) + center.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (getClass() != object.getClass()) return false;
    GdxBoundingSphere other = (GdxBoundingSphere)object;
    return radius == other.radius && center.equals(other.center);
  }

  public GdxBoundingSphere inscribe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    center.x = maxX - minX;
    center.y = maxY - minY;
    center.z = maxZ - minZ;
    radius = Math.min(maxX - center.x, Math.min(maxY - center.y, maxZ - center.z));
    return this;
  }
  
  public GdxBoundingSphere inscribe(BoundingBox bounds) {
    return inscribe(bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z);
  }
  
  public GdxBoundingSphere circumscribe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    float a = center.x = maxX - minX;
    float b = center.y = maxY - minY;
    float c = center.z = maxZ - minZ;
    radius = (float)(Math.max(Math.hypot(a, b), Math.max(Math.hypot(b, c), Math.hypot(a, c))) * 0.5);
    return this;
  }
  
  public GdxBoundingSphere circumscribe(BoundingBox bounds) {
    return circumscribe(bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z);
  }
  
  public void translate(float x, float y, float z) {
    center.x += x;
    center.y += y;
    center.z += z;
  }
  
  public void scale(float x, float y, float z) {
    float scale = x > y ? x : (y > z ? y : z);
    float extension = component.getInteractiveAreaExtension();
    float scaledRadius = (radius - extension) * scale + extension;
    float radiusDiff = scaledRadius - radius;
    center.x += radiusDiff;
    center.y += radiusDiff;
    center.z += radiusDiff;
    radius = scaledRadius;
  }
  
  public boolean intersectRay(Ray pickingRay, Vector3 intersection) {
    float x = intersection.x;
    float y = intersection.y;
    float z = intersection.z;
    if (Intersector.intersectRaySphere(pickingRay, center, radius, intersection)) {
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
        modelBuilder.createSphere(1, 1, 1, 32, 32, material, attributes),
        center.x, center.y, center.z, 2 * radius, 2 * radius, 2 * radius) {
          @Override
          public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
            setCenter(center);
            setScale(2 * radius);
            super.getRenderables(renderables, pool);
          }
    };
  }
}
