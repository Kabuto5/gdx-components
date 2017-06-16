package components.abstracts;

import components.aggregated.GdxListeners;
import components.listeners.GdxClickListener;

public abstract class GdxAbstractButton extends GdxAbstractComponent {
  private final GdxListeners<GdxClickListener> clickListeners = 
      new GdxListeners<GdxClickListener>();

  public GdxAbstractButton(float x, float y, float width, float height) {
    super(x, y, width, height);
    makeActive();
  }

  protected void addClickListener(Object tag, GdxClickListener listener) {
    clickListeners.add(tag, listener);
  }
  
  protected boolean removeClickListener(GdxClickListener listener) {
    return clickListeners.remove(listener);
  }
  
  protected GdxClickListener removeClickListener(Object tag) {
    return clickListeners.remove(tag);
  }
  
  private void launchClickEvent() {
    for (GdxClickListener listener : clickListeners) {
      listener.onClick(this);
    }
  }

  @Override
  protected void onPressed(float x, float y, int pointer) {
    makeDirty();
  }

  @Override
  protected void onReleased(float x, float y, int pointer) {
    if (!isDragged()) {
      launchClickEvent();
    }
    makeDirty();
  }
}
