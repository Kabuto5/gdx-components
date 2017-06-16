package io;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

/**
 * An interface for an OpenGL shader implemented through GdxComponents library.
 * It's implemented by {@link effects.GdxShaderEffect GdxShaderEffect}, as any
 * visual effect using a shader needs to implement this interface to be recognized
 * as such while drawing.
 * <p>
 * Be aware that support of shaders is highly experimental and not at all optimized.
 * Especially multiple shaders on a single component or multipass shaders should be avoided
 * or thoroughly tested.
 *
 */
public interface GdxShader {
  /**
   * Returns an area of canvas on which the shader is applied.
   * @return The area in canvas coordinates
   */
  public Rectangle getArea();

  /**
   * Returns an instance of ShaderProgram used by this shader.
   * @return The instance of ShaderProgram used
   */
  public ShaderProgram getProgram();
  
  /**
   * Set parameters of the ShaderProgram in this method.
   * @param painter A painter which will apply the shader
   * @param canvasWidth Width of the area the shader is applied on in canvas units
   * @param canvasHeight Height of the area the shader is applied on in canvas units
   */
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight);
  
  public static final String VERTEX_PASSTHROUGH =
      "precision mediump float;\n" +

      "attribute vec4 a_color;\n" +
      "attribute vec3 a_position;\n" +
      "attribute vec2 a_texCoord0;\n" +
      
      "uniform mat4 u_projTrans;\n" +

      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "void main() {\n" +
      "  v_color = a_color;\n" +
      "  v_texCoord0 = a_texCoord0;\n" +
      "  gl_Position = u_projTrans * vec4(a_position, 1.0);\n" +
      "}\n";
  public static final String FRAGMENT_PASSTHROUGH =
      "precision lowp float;\n" +

      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoord0;\n" +

      "uniform sampler2D u_texture;\n" +

      "void main() {\n" +
      "  gl_FragColor = texture2D(u_texture, v_texCoord0) * v_color;\n" +
      "}\n";
}
