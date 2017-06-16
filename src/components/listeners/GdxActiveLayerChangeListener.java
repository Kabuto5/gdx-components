package components.listeners;

import components.GdxLayerContainer;

public interface GdxActiveLayerChangeListener extends GdxComponentListener {
  public void onActiveLayerChange(GdxLayerContainer sender, int previousLayer, int currentLayer);
}
