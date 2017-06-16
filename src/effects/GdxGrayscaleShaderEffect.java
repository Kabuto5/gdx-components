package effects;

import io.GdxPainter;
import io.GdxShader;

import components.interfaces.GdxComponent;

public class GdxGrayscaleShaderEffect extends GdxSimpleShaderEffect {
  private float intensity;

  public GdxGrayscaleShaderEffect(GdxComponent component, float intensity) {
    super(component, GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_GRAYSCALE);
    setIntensity(intensity);
  }

  public float getIntensity() {
    return intensity;
  }

  public void setIntensity(float intensity) {
    this.intensity = intensity;
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) {
    getProgram().setUniformf("u_intensity", intensity);
  }
  
  public static final String FRAGMENT_GRAYSCALE =
      "precision lowp float;\n" +
  
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +
      "uniform float u_intensity;\n" +

      "void main() {\n" +
      "  vec4 color = texture2D(u_texture, v_texCoord0) * v_color;\n" +
      "  vec3 grayscale = vec3((color.r + color.g + color.b) / 3.0);\n" +
      "  color.rgb = mix(color.rgb, grayscale, u_intensity);\n" +
      "  gl_FragColor = color;\n" +
      "}";
}
