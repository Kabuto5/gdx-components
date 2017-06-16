package components.listeners;

import components.interfaces.GdxComponent;

public interface GdxPositionChangeListener extends GdxComponentListener {
  public void onPositionChange(GdxComponent sender, float position);
}