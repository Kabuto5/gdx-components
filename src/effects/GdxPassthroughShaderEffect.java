package effects;

import io.GdxPainter;
import io.GdxShader;

import components.interfaces.GdxComponent;

/**
 * The basic shader which has no effect on rendering. For most intents and purposes,
 * it acts as adding no shader based effect at all, except that it causes all subsequent
 * effects to be rendered through a framebuffer.
 * <p>
 * It's meant primarily for testing purposes, but it may also lead to desirable results
 * on some occasions.
 *
 */
public class GdxPassthroughShaderEffect extends GdxSimpleShaderEffect {
  public GdxPassthroughShaderEffect(GdxComponent component) {
    super(component, GdxShader.VERTEX_PASSTHROUGH, GdxShader.FRAGMENT_PASSTHROUGH);
  }

  @Override
  public void prepare(GdxPainter painter, float canvasWidth, float canvasHeight) { }
}
