package helpers;

import general.GdxModelInstance;

import java.util.Collection;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

public class ModelUtils {
  /**
   * Determines the width of a box bounding a given group of models.
   * @param models Group of models to be worked with
   * @return Width of the bounding box
   */
  public static float calculateBoundingBoxWidth(Collection<GdxModelInstance> models) {
    float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
    for (GdxModelInstance model : models) {
      if (model.getMinX() < minX) minX = model.getMinX();
      if (model.getMaxX() > maxX) maxX = model.getMaxX();
    }
    return maxX - minX;
  }
  
  /**
   * Determines the height of a box bounding a given group of models.
   * @param models Group of models to be worked with
   * @return Height of the bounding box
   */
  public static float calculateBoundingBoxHeight(Collection<GdxModelInstance> models) {
    float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
    for (GdxModelInstance model : models) {
      if (model.getMinY() < minY) minY = model.getMinY();
      if (model.getMaxY() > maxY) maxY = model.getMaxY();
    }
    return maxY - minY;
  }
  
  /**
   * Determines the depth of a box bounding a given group of models.
   * @param models Group of models to be worked with
   * @return Depth of the bounding box
   */
  public static float calculateBoundingBoxDepth(Collection<GdxModelInstance> models) {
    float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
    for (GdxModelInstance model : models) {
      if (model.getMinZ() < minZ) minZ = model.getMinZ();
      if (model.getMaxZ() > maxZ) maxZ = model.getMaxZ();
    }
    return maxZ - minZ;
  }
  
  public static Model createBox(ModelBuilder modelBuilder, Material topMaterial, Material northMaterial, Material eastMaterial, 
      Material southMaterial, Material westMaterial, Material bottomMaterial) {
    Matrix4 transformMatrix = new Matrix4();
    modelBuilder.begin();
    MeshPartBuilder partBuilder;
    Material partMaterial = topMaterial;
    partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    partBuilder.setVertexTransform(transformMatrix.setToRotation(0, 0, 1, 90));
    partBuilder.rect(-0.5f,  0.5f,  0.5f,  -0.5f, -0.5f,  0.5f,   0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,   0f,  0f,  1f);
    if (northMaterial != partMaterial) {
      partMaterial = northMaterial;
      partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    }
    partBuilder.rect(-0.5f,  0.5f,  0.5f,   0.5f,  0.5f,  0.5f,   0.5f,  0.5f, -0.5f,  -0.5f,  0.5f, -0.5f,   0f,  1f,  0f);
    if (eastMaterial != partMaterial) {
      partMaterial = eastMaterial;
      partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    }
    partBuilder.rect( 0.5f,  0.5f,  0.5f,   0.5f, -0.5f,  0.5f,   0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0f,  0f,  1f);
    if (southMaterial != partMaterial) {
      partMaterial = southMaterial;
      partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    }
    partBuilder.rect(-0.5f, -0.5f, -0.5f,   0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,  -0.5f, -0.5f,  0.5f,   0f, -1f,  0f);
    if (westMaterial != partMaterial) {
      partMaterial = westMaterial;
      partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    }
    partBuilder.setVertexTransform(transformMatrix.setToRotation(1, 0, 0, 90));
    partBuilder.rect(-0.5f,  0.5f,  0.5f,  -0.5f,  0.5f, -0.5f,  -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f,  0.5f,  -1f,  0f,  0f);
    if (bottomMaterial != partMaterial) {
      partMaterial = bottomMaterial;
      partBuilder = modelBuilder.part(null, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, partMaterial);
    }
    partBuilder.setVertexTransform(transformMatrix.setToRotation(0, 0, 1, 180));
    partBuilder.rect(-0.5f,  0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f, -0.5f, -0.5f,  -0.5f, -0.5f, -0.5f,   0f,  0f,  1f);
    return modelBuilder.end();
  }
}
