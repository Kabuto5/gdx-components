package effects;

import helpers.ShaderFactory;
import io.GdxPainter;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import components.interfaces.GdxComponent;

public abstract class GdxSimpleShaderEffect extends GdxShaderEffect {
  private ShaderProgram program;
  
  public GdxSimpleShaderEffect(GdxComponent component, String vertexShader, String fragmentShader) {
    super(component);
    program = ShaderFactory.createShaderProgram(vertexShader, fragmentShader);
  }

  @Override
  public ShaderProgram getProgram() {
    return program;
  }

  @Override
  public void before(float x, float y, GdxPainter painter) {
    super.before(x, y, painter);
    painter.pushShader(this);
  }

  @Override
  public void after(float x, float y, GdxPainter painter) {
    painter.popShader();
    super.after(x, y, painter);
  }

  @Override
  public void dispose() {
    program.dispose();
  }
}
