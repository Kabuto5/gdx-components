package components;

import helpers.ModelUtils;
import io.GdxPainter;
import general.GdxModelInstance;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

import components.abstracts.GdxAbstractComponent3D;

public class GdxBox3D extends GdxAbstractComponent3D {
  protected final GdxModelInstance modelInstance;
  
  public GdxBox3D(Material topMaterial, Material northMaterial, Material eastMaterial, 
      Material southMaterial, Material westMaterial, Material bottomMaterial, 
      float x, float y, float z, float width, float height, float depth) {
    super(new GdxModelInstance(
        ModelUtils.createBox(getModelBuilder(), topMaterial, northMaterial, eastMaterial, 
            southMaterial, westMaterial, bottomMaterial), 
        width / 2, height / 2, depth / 2, 
        width, height, depth),
        x, y, z);
    modelInstance = getModels().iterator().next();
  }
  
  public GdxBox3D(Material topMaterial, Material sideMaterial, float x, float y, float z, 
      float width, float height, float depth) {
    this(topMaterial, sideMaterial, sideMaterial, sideMaterial, sideMaterial, sideMaterial, 
        x, y, z, width, height, depth);
  }
  
  public GdxBox3D(Material material, float x, float y, float z, 
      float width, float height, float depth) {
    this(material, material, x, y, z, width, height, depth);
  }

  @Override
  public void dispose() {
    modelInstance.getModel().dispose();
    super.dispose();
  }
}
