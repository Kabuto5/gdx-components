package components.listeners;

import com.badlogic.gdx.math.Vector2;
import components.interfaces.GdxComponent;

public interface GdxScaleChangeListener extends GdxComponentListener {
  /**
   * This method is called whenever the component's current scale is changed.
   * As a result, it might be called quite frequently during continuous rescaling.
   * @param sender Component concerned
   * @param zoom Current zoom
   * @param zoomAt Point at which an user is zooming
   */
  public void onScaleChange(GdxComponent sender, float zoom, Vector2 zoomAt);
  
  /**
   * This method is called when the component's rescaling was finished for the
   * time being and its current scale is expected to stay constant for some time.
   * @param sender Component concerned
   * @param zoom Current zoom
   * @param zoomAt Point at which an user is zooming
   */
  public void onRescaled(GdxComponent sender, float zoom, Vector2 zoomAt);
}