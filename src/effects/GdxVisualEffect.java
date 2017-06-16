package effects;

import io.GdxInputListener;
import io.GdxPainter;

import com.badlogic.gdx.utils.Disposable;
import components.interfaces.GdxComponent;
import components.interfaces.GdxUpdatable;

/**
 * This is designed to create generalized visual effects, which can then be used for
 * various different components. Mainly, it can draw something under or over the 
 * component. It can also reconfigure the painter or the component itself before 
 * drawing, but be aware that the component may override those settings afterwards.
 *
 */
public interface GdxVisualEffect extends GdxUpdatable, GdxInputListener, Disposable {
  public GdxComponent getComponent();

  /**
   * This method is called before the component itself is drawn.
   * @param x component's coordinate relative to its frame's origin
   *          (same as {@link GdxComponent#getFrameX() getFrameX}
   *          method's result)
   * @param y component's coordinate relative to its frame's origin
   *          (same as {@link GdxComponent#getFrameY() getFrameY}
   *          method's result)
   * @param painter Provides access to drawing operations
   */
  public void before(float x, float y, GdxPainter painter);
  
  /**
   * This method is called after the component itself is drawn.
   * @param x component's coordinate relative to its frame's origin
   *          (same as {@link GdxComponent#getFrameX() getFrameX}
   *          method's result)
   * @param y component's coordinate relative to its frame's origin
   *          (same as {@link GdxComponent#getFrameY() getFrameY}
   *          method's result)
   * @param painter Provides access to drawing operations
   */
  public void after(float x, float y, GdxPainter painter);
  
  /**
   * Override this if you're using any internal sources which needs to be disposed.
   */
  @Override
  public void dispose();
}
