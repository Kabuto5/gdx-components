package effects;

import io.GdxPainter;
import io.GdxShader;

import components.interfaces.GdxComponent;

public class GdxNegativeShaderEffect extends GdxSimpleShaderEffect {
  public GdxNegativeShaderEffect(GdxComponent component) {
    super(component, GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_NEGATIVE);
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) { }
  
  public static final String FRAGMENT_NEGATIVE =
      "precision lowp float;\n" +
  
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +

      "void main() {\n" +
      "  vec4 color = texture2D(u_texture, v_texCoord0) * v_color;\n" +
      "  color.rgb = 1.0 - color.rgb;\n" +
      "  gl_FragColor = color;\n" +
      "}";
}
