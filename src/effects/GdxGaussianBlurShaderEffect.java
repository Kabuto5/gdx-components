package effects;

import helpers.ShaderFactory;
import io.GdxPainter;
import io.GdxShader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import components.interfaces.GdxComponent;

public class GdxGaussianBlurShaderEffect extends GdxShaderEffect {
  private ShaderProgram program;
  private float blurRadius;
  private boolean firstPass = true;

  public GdxGaussianBlurShaderEffect(GdxComponent component, float blurRadius) {
    super(component);
    program = ShaderFactory.createShaderProgram(
        GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_GAUSSIAN);
    setBlurRadius(blurRadius);
  }

  @Override
  public ShaderProgram getProgram() {
    return program;
  }

  public float getBlurRadius() {
    return blurRadius;
  }

  public void setBlurRadius(float radius) {
    this.blurRadius = radius;
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) {
    if (firstPass) {
      program.setUniformf("u_direction", 1f, 0f);
      program.setUniformf("u_resolution", painter.getScreenWidth());
      program.setUniformf("u_radius", blurRadius);
    } else {
      program.setUniformf("u_direction", 0f, 1f);
      program.setUniformf("u_resolution", painter.getScreenHeight());
    }
    firstPass = !firstPass;
  }

  @Override
  public void before(float x, float y, GdxPainter painter) {
    super.before(x, y, painter);
    painter.pushShader(this);
    painter.pushShader(this);
  }

  @Override
  public void after(float x, float y, GdxPainter painter) {
    painter.popShader();
    painter.popShader();
    super.after(x, y, painter);
  }

  @Override
  public void dispose() {
    program.dispose();
  }
  
  public static final String FRAGMENT_GAUSSIAN =
      "precision mediump float;\n" +
  
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +
      "uniform float u_resolution;\n" +
      "uniform float u_radius;\n" +
      "uniform vec2 u_direction;\n" +

      "void main() {\n" +
      "  vec2 texCoord0 = v_texCoord0;\n" +
      "  vec4 sum = vec4(0.0);\n" +
      
      "  float distance = u_radius / u_resolution;\n" +
      "  float horz_step = u_direction.x;\n" +
      "  float vert_step = u_direction.y;\n" +

      "  sum += texture2D(u_texture, vec2(texCoord0.x - 4.0 * distance * horz_step, texCoord0.y - 4.0 * distance * vert_step)) * 0.0162162162;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x - 3.0 * distance * horz_step, texCoord0.y - 3.0 * distance * vert_step)) * 0.0540540541;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x - 2.0 * distance * horz_step, texCoord0.y - 2.0 * distance * vert_step)) * 0.1216216216;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x - 1.0 * distance * horz_step, texCoord0.y - 1.0 * distance * vert_step)) * 0.1945945946;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x, texCoord0.y)) * 0.2270270270;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x + 1.0 * distance * horz_step, texCoord0.y + 1.0 * distance * vert_step)) * 0.1945945946;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x + 2.0 * distance * horz_step, texCoord0.y + 2.0 * distance * vert_step)) * 0.1216216216;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x + 3.0 * distance * horz_step, texCoord0.y + 3.0 * distance * vert_step)) * 0.0540540541;\n" +
      "  sum += texture2D(u_texture, vec2(texCoord0.x + 4.0 * distance * horz_step, texCoord0.y + 4.0 * distance * vert_step)) * 0.0162162162;\n" +

      "  gl_FragColor = sum * v_color;\n" +
      "}\n";
}
