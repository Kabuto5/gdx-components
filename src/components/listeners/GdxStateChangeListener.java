package components.listeners;

import components.interfaces.GdxComponent;

public interface GdxStateChangeListener extends GdxComponentListener {
  public void onStateChange(GdxComponent sender, boolean isOn);
}