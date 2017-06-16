package effects;

import io.GdxPainter;
import io.GdxShader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import components.interfaces.GdxComponent;

/**
 * A base class for a visual effect which implements an OpenGL shader.
 * <p>
 * Be aware that support of shaders is highly experimental and not at all optimized.
 * Especially multiple shaders on a single component or multipass shaders should be avoided
 * or thoroughly tested.
 *
 */
public abstract class GdxShaderEffect extends GdxAbstractVisualEffect implements GdxShader {
  public GdxShaderEffect(GdxComponent component) {
    super(component);
  }

  public void before(float x, float y, GdxPainter painter) {
    super.before(x, y, painter);
    ShaderProgram.pedantic = false;
  }

  public Rectangle getArea() {
    return getComponent().getFrameArea();
  }
}
