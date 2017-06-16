package effects;

import io.GdxPainter;
import io.GdxShader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import components.interfaces.GdxComponent;

public class GdxVignetteShaderEffect extends GdxSimpleShaderEffect {
  private static final float DEFAULT_INTENSITY = 1.0f;
  private static final float DEFAULT_OUTER_EDGE = 0.5f;
  
  private float left, top;
  private float intensity;
  private float radius;
  private float outerEdge, innerEdge;

  public GdxVignetteShaderEffect(GdxComponent component, float intensity,
      float radius, float outerEdge, float innerEdge) {
    super(component, GdxShader.VERTEX_PASSTHROUGH, FRAGMENT_VIGNETTE);
    setIntensity(intensity);
    setRadius(radius);
    setEdges(outerEdge, innerEdge);
  }
  
  public GdxVignetteShaderEffect(GdxComponent component, float intensity,
      float radius, float edgeRange) {
    this(component, intensity, radius, DEFAULT_OUTER_EDGE, 
        DEFAULT_OUTER_EDGE - edgeRange);
  }
  
  public GdxVignetteShaderEffect(GdxComponent component, float radius, 
      float edgeRange) {
    this(component, DEFAULT_INTENSITY, radius, edgeRange);
  }

  public float getIntensity() {
    return intensity;
  }

  public void setIntensity(float intensity) {
    this.intensity = intensity;
  }
  
  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public float getOuterEdge() {
    return outerEdge;
  }

  public void setOuterEdge(float outerEdge) {
    setEdges(outerEdge, innerEdge);
  }

  public float getInnerEdge() {
    return innerEdge;
  }

  public void setInnerEdge(float innerEdge) {
    setEdges(outerEdge, innerEdge);
  }

  public void setEdges(float outerEdge, float innerEdge) {
    if (outerEdge < innerEdge)
      throw new IllegalArgumentException(String.format(
          "Outer edge is closer than inner edge (%0.2f < %0.2f)",
          outerEdge, innerEdge));
    this.outerEdge = outerEdge;
    this.innerEdge = innerEdge;
  }
  
  public float getEdgeRange() {
    return outerEdge - innerEdge;
  }
  
  @Override
  public void before(float x, float y, GdxPainter painter) {
    super.before(x, y, painter);
    left = x;
    top = y;
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) {
    GdxComponent component = getComponent();
    ShaderProgram program = getProgram();
    float ppcu = painter.getPpcu();
    float width = component.getWidth();
    float height = component.getHeight();
    program.setUniformf("u_origin", 
        left * ppcu + painter.getOriginLeft(), 
        (canvasHeight - top - height) * ppcu + painter.getOriginTop());
    program.setUniformf("u_size", width * ppcu, height * ppcu);
    program.setUniformf("u_intensity", intensity);
    program.setUniformf("u_corners", 0.5f - radius);
    program.setUniformf("u_outerEdge", outerEdge);
    program.setUniformf("u_innerEdge", innerEdge);
  }

  public static final String FRAGMENT_VIGNETTE =
      "precision lowp float;\n" +
          
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +
      "uniform vec2 u_origin;\n" +
      "uniform vec2 u_size;\n" +
      "uniform float u_intensity;\n" +
      "uniform float u_corners;\n" +
      "uniform float u_outerEdge;\n" +
      "uniform float u_innerEdge;\n" +

      "void main() {\n" +
      "  vec4 color = texture2D(u_texture, v_texCoord0) * v_color;\n" +
      "  vec2 position = gl_FragCoord.xy - u_origin;\n" +
      "  vec2 axialDistance = abs(position / u_size - 0.5);\n" +
      "  float vignette;\n" +
      "  if (axialDistance.x <= u_corners || axialDistance.y <= u_corners) {\n" +
      "    float distance = max(axialDistance.x, axialDistance.y);\n" +
      "    vignette = smoothstep(u_outerEdge, u_innerEdge, distance);\n" +
      "  } else {\n" +
      "    float distance = length(axialDistance - u_corners);\n" +
      "    float cornerEdge = u_outerEdge * (1.0 - 2.0 * u_corners);\n" +
      "    vignette = smoothstep(cornerEdge, cornerEdge - (u_outerEdge - u_innerEdge), distance);\n" +
      "  }\n" +
      "  color.a = mix(color.a, color.a * vignette, u_intensity);\n" +
      "  gl_FragColor = color;\n" +
      "}\n";
}
