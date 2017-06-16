package effects;

import io.GdxPainter;
import io.GdxShader;

import components.interfaces.GdxComponent;

public class GdxRippleShaderEffect extends GdxSimpleShaderEffect {
  private float time;
  
  public GdxRippleShaderEffect(GdxComponent component) {
    super(component, GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_RIPPLE);
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) {
    getProgram().setUniformf("u_time", time);
  }
  
  @Override
  public void step(float delay) {
    super.step(delay);
    time = (time + delay) % 1;
    makeDirty();
  }

  public static final String FRAGMENT_RIPPLE =
      "precision lowp float;\n" +
  
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +
      "uniform float u_time;\n" +

      "void main() {\n" +
      "  vec2 p = -1.0 + 2.0 * v_texCoord0;\n" +
      //"  vec2 p = v_texCoord0;\n" +
      "  float len = length(p);\n" +
      //"  vec2 uv = v_texCoord0 + (p / len) * cos(len * 12.0 - u_time * 4.0) * 0.03;\n" +
      "  vec2 uv = v_texCoord0;\n" +
      "  uv = v_texCoord0 + (p / len) * sin(len * 6.0 / u_time * 0.1) / len / u_time * 0.05;\n" +
      "  gl_FragColor = texture2D(u_texture, uv) * v_color;\n" +
      "}";
}
