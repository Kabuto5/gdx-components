package components.interfaces;

import com.badlogic.gdx.math.Vector3;

public interface GdxComponent3D extends GdxComponent {
  public float getZ();

  public void setZ(float z);

  public Vector3 getLocation(Vector3 out);
  
  public void setLocation(float x, float y, float z);

  public void setLocation(Vector3 location);
  
  public float getDepth();
  
  public void setDepth(float depth);

  public Vector3 getSize(Vector3 out);

  public void setSize(float width, float height, float depth);

  public void setSize(Vector3 size);
}
