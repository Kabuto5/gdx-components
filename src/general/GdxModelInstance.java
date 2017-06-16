package general;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import components.bounds.GdxBoundingBox;

/**
 * A wrapping class which makes a standard ModelInstance a little bit bulkier
 * and easier to use.
 * <p>
 * Mainly, it makes translations independent of current scale and rotation,
 * which is generally desireable behavior in a given context.
 * It also takes transformations into account while calculating a bounding box,
 * therefore representing it as it currently is within the world coordinates,
 * rathen than using the original coordinates of the model with no transformations.
 *
 */
public class GdxModelInstance implements RenderableProvider {
  private final ModelInstance modelInstance;
  private final Vector3 center;
  private final Vector3 scale;
  private final Vector3 rotation;
  private final Vector3 size;
  private final Matrix4 boundingBoxTransform = new Matrix4();

  public GdxModelInstance(Model model, float centerX, float centerY, float centerZ, 
      float scaleX, float scaleY, float scaleZ, float rotationX, float rotationY, float rotationZ) {
    modelInstance = new ModelInstance(model);
    center = new Vector3(centerX, centerY, centerZ);
    scale = new Vector3(scaleX, scaleY, scaleZ);
    rotation = new Vector3(rotationX, rotationY, rotationZ);
    size = new Vector3();
    calculateTransformation();
    calculateSize();
  }
  
  public GdxModelInstance(Model model, float centerX, float centerY, float centerZ, 
      float scaleX, float scaleY, float scaleZ) {
    this(model, centerX, centerY, centerZ, scaleX, scaleY, scaleZ, 0, 0, 0);
  }
  
  public GdxModelInstance(Model model, float centerX, float centerY, float centerZ) {
    this(model, centerX, centerY, centerZ, 1, 1, 1);
  }
  
  public GdxModelInstance(Model model) {
    this(model, 0, 0, 0);
  }
  
  /**
   * Returns the underlying ModelInstance to access any functionality not propagated
   * into this wrapping class. Please note that transformations are managed by the 
   * wrapping class and any changes you make in that regard may be overriden at any time.
   * @return The underlying ModelInstance
   */
  public Model getModel() {
    return modelInstance.model;
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

  public void setCenter(float centerX, float centerY, float centerZ) {
    center.x = centerX;
    center.y = centerY;
    center.z = centerZ;
    calculateTransformation();
  }

  public void setCenter(Vector3 center) {
    this.center.x = center.x;
    this.center.y = center.y;
    this.center.z = center.z;
    calculateTransformation();
  }
  
  public float getMinX() {
    return center.x - size.x * 0.5f;
  }
  
  public float getMinY() {
    return center.y - size.y * 0.5f;
  }
  
  public float getMinZ() {
    return center.z - size.z * 0.5f;
  }
  
  public float getMaxX() {
    return center.x + size.x * 0.5f;
  }
  
  public float getMaxY() {
    return center.y + size.y * 0.5f;
  }
  
  public float getMaxZ() {
    return center.z + size.z * 0.5f;
  }

  public float getScaleX() {
    return scale.x;
  }

  public float getScaleY() {
    return scale.y;
  }

  public float getScaleZ() {
    return scale.z;
  }

  public void setScale(float scaleX, float scaleY, float scaleZ) {
    center.x += (scaleX / this.scale.x * size.x - size.x) * 0.5f;
    center.y += (scaleY / this.scale.y * size.y - size.y) * 0.5f;
    center.z += (scaleZ / this.scale.z * size.z - size.z) * 0.5f;
    scale.x = scaleX;
    scale.y = scaleY;
    scale.z = scaleZ;
    calculateTransformation();
    size.x *= scaleX / this.scale.x;
    size.y *= scaleY / this.scale.y;
    size.z *= scaleZ / this.scale.z;
  }

  public void setScale(Vector3 scale) {
    setScale(scale.x, scale.y, scale.z);
  }
  
  public void setScale(float scale) {
    setScale(scale, scale, scale);
  }

  public float getRotationX() {
    return rotation.x;
  }

  public float getRotationY() {
    return rotation.y;
  }

  public float getRotationZ() {
    return rotation.z;
  }

  public void setRotation(float rotationX, float rotationY, float rotationZ) {
    rotation.x = rotationX;
    rotation.y = rotationY;
    rotation.z = rotationZ;
    calculateTransformation();
    calculateSize();
  }

  public void setRotation(Vector3 rotation) {
    this.rotation.x = rotation.x;
    this.rotation.y = rotation.y;
    this.rotation.z = rotation.z;
    calculateTransformation();
    calculateSize();
  }

  public float getWidth() {
    return size.x;
  }
  
  public float getHeight() {
    return size.y;
  }
  
  public float getDepth() {
    return size.z;
  }
  
  public void setSize(float sizeX, float sizeY, float sizeZ) {
    center.x += (sizeX - size.x) * 0.5f;
    center.y += (sizeY - size.y) * 0.5f;
    center.z += (sizeZ - size.z) * 0.5f;
    scale.x *= sizeX / size.x;
    scale.y *= sizeY / size.y;
    scale.z *= sizeZ / size.z;
    size.x = sizeX;
    size.y = sizeY;
    size.z = sizeZ;
    calculateTransformation();
  }
  
  public void setSize(Vector3 size) {
    setSize(size.x, size.y, size.z);
  }
  
  public void setMaterial(int nodeIndex, int partIndex, Iterable<Attribute> material) {
    modelInstance.nodes.get(nodeIndex).parts.get(partIndex).material.set(material);
  }
  
  public void setMaterial(int nodeIndex, int partIndex, Attribute... material) {
    modelInstance.nodes.get(nodeIndex).parts.get(partIndex).material.set(material);
  }
  
  private void calculateSize() {
    BoundingBox out = new BoundingBox();
    modelInstance.calculateBoundingBox(out);
    boundingBoxTransform.setToScaling(scale).
        rotate(1, 0, 0, rotation.x).
        rotate(0, 1, 0, rotation.y).
        rotate(0, 0, 1, rotation.z);
    out.mul(modelInstance.transform);
    size.x = out.max.x - out.min.x;
    size.y = out.max.y - out.min.y;
    size.z = out.max.z - out.min.z;
  }
  
  private void calculateTransformation() {
    modelInstance.transform.setToTranslationAndScaling(
        center.x, - center.y, center.z, 
        scale.x, scale.y, scale.z).
        rotate(1, 0, 0, rotation.x).
        rotate(0, 1, 0, rotation.y).
        rotate(0, 0, 1, rotation.z);
  }
  
  public GdxModelInstance translate(float x, float y, float z) {
    center.x += x;
    center.y += y;
    center.z += z;
    calculateTransformation();
    return this;
  }
  
  public GdxModelInstance scale(float scaleX, float scaleY, float scaleZ) {
    center.x += (scaleX * size.x - size.x) * 0.5f;
    center.y += (scaleY * size.y - size.y) * 0.5f;
    center.z += (scaleZ * size.z - size.z) * 0.5f;
    scale.x *= scaleX;
    scale.y *= scaleY;
    scale.z *= scaleZ;
    size.x *= scaleX;
    size.y *= scaleY;
    size.z *= scaleZ;
    calculateTransformation();
    return this;
  }
  
  public GdxModelInstance scale(float scale) {
    return scale(scale, scale, scale);
  }
  
  public GdxModelInstance rotate(float angleX, float angleY, float angleZ) {
    rotation.x = (rotation.x + angleX) % 360;
    rotation.y = (rotation.y + angleY) % 360;
    rotation.z = (rotation.z + angleZ) % 360;
    calculateTransformation();
    calculateSize();
    return this;
  }
  
  public GdxModelInstance pivotRotate(float pivotX, float pivotY, float pivotZ, float axisX, float axisY, float axisZ, float angle) {
    //Nullify existing facing angle for proper pivot rotation
    modelInstance.transform.setToTranslationAndScaling(
        center.x, - center.y, center.z, 
        scale.x, scale.y, scale.z);
    //Rotate model around the pivot point
    float offsetX = center.x - pivotX;
    float offsetY = center.y - pivotY;
    float offsetZ = center.z - pivotZ;
    modelInstance.transform.translate(offsetX, - offsetY, offsetZ).
        rotate(axisX, axisY, axisZ, angle).
        translate(- offsetX, offsetY, - offsetZ).
        //Incorporate previous facing angle
        rotate(1, 0, 0, rotation.x).
        rotate(0, 1, 0, rotation.y).
        rotate(0, 0, 1, rotation.z);
    //Update center of the model for future transformation recalculations
    modelInstance.transform.getTranslation(center);
    //Update facing angle for future transformation recalculations
    rotation.x = (rotation.x + axisX * angle) % 360;
    rotation.y = (rotation.y + axisY * angle) % 360;
    rotation.z = (rotation.z + axisZ * angle) % 360;
    calculateSize();
    return this;
  }
  
  public GdxModelInstance pivotRotateX(float pivotX, float pivotY, float pivotZ, float angle) {
    return pivotRotate(pivotX, pivotY, pivotZ, 1, 0, 0, angle);
  }
  
  public GdxModelInstance pivotRotateY(float pivotX, float pivotY, float pivotZ, float angle) {
    return pivotRotate(pivotX, pivotY, pivotZ, 0, 1, 0, angle);
  }
  
  public GdxModelInstance pivotRotateZ(float pivotX, float pivotY, float pivotZ, float angle) {
    return pivotRotate(pivotX, pivotY, pivotZ, 0, 0, 1, angle);
  }
  
  public GdxBoundingBox getBoundingBox(GdxBoundingBox out) {
    float extension = out.getComponent().getInteractiveAreaExtension();
    out.min.x = getMinX() - extension;
    out.min.y = getMinY() - extension;
    out.min.z = getMinZ() - extension;
    out.max.x = getMaxX() + extension;
    out.max.y = getMaxY() + extension;
    out.max.z = getMaxZ() + extension;
    return out;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    modelInstance.getRenderables(renderables, pool);
  }

  @Override
  public String toString() {
    return GdxModelInstance.class.getSimpleName() +
        " (center = " + center +
        ", scale = " + scale +
        ", rotation = " + rotation +
        ", size = " + size + ")";
  }
}
