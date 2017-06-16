package components;

import general.GdxModelInstance;
import helpers.collections.SingleElementCollection;

import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import components.abstracts.GdxAbstractComponent3D;
import components.bounds.GdxBoundingBox;
import components.bounds.GdxBoundingShape;
import components.bounds.GdxBoundingSphere;

public class GdxDial3D extends GdxAbstractComponent3D {
  private final Vector3 axis;
  private float angle;
  private float lastIntersectionZ;
  
  public GdxDial3D(Collection<GdxModelInstance> models, float x, float y, float z, Vector3 axis) {
    super(models, x, y, z);
    this.axis = axis;
  }

  public GdxDial3D(float x, float y, float z, Vector3 axis, GdxModelInstance... models) {
    super(x, y, z, models);
    this.axis = axis;
  }

  public GdxDial3D(GdxModelInstance model, float x, float y, float z, Vector3 axis) {
    super(model, x, y, z);
    this.axis = axis;
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
    rotateModels(axis, angle);
  }

  @Override
  protected Collection<GdxBoundingShape> updateBounds(Collection<GdxModelInstance> models, Collection<GdxBoundingShape> bounds) {
    BoundingBox boundingBox = new BoundingBox();
    GdxBoundingBox out = new GdxBoundingBox(this);
    for (GdxModelInstance model : models) {
      model.getBoundingBox(out);
      boundingBox.ext(out);
    }
    return new SingleElementCollection<GdxBoundingShape>(new GdxBoundingSphere(this).circumscribe(boundingBox));
  }

  @Override
  public boolean intersectRay(Ray pickingRay, Vector3 intersection) {
    if (super.intersectRay(pickingRay, intersection)) {
      lastIntersectionZ = intersection.z;
      return true;
    }
    return false;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    Vector2 coords = new Vector2(getFrameX() + x, lastIntersectionZ);
    Vector2 prevCoords = new Vector2(coords.x - differenceX, lastIntersectionZ);
    System.out.println(coords.angle(prevCoords));
    return true;
  }
}
