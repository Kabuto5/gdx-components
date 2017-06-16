package components.abstracts;

import components.aggregated.GdxListeners;
import components.listeners.GdxStateChangeListener;

public abstract class GdxAbstractToggleButton extends GdxAbstractComponent {
  private final GdxListeners<GdxStateChangeListener> stateChangeListeners = 
      new GdxListeners<GdxStateChangeListener>();
  private boolean isOn;
  
  public GdxAbstractToggleButton(float x, float y, float width, float height, boolean startOn) {
    super(x, y, width, height);
    makeActive();
    isOn = startOn;
  }
  
  protected void addStateChangeListener(Object tag, GdxStateChangeListener listener) {
    stateChangeListeners.add(tag, listener);
  }
  
  protected boolean removeStateChangeListener(GdxStateChangeListener listener) {
    return stateChangeListeners.remove(listener);
  }
  
  protected GdxStateChangeListener removeStateChangeListener(Object tag) {
    return stateChangeListeners.remove(tag);
  }
  
  protected boolean isOn() {
    return isOn;
  }
  
  protected void setOn(boolean isOn) {
    if (this.isOn != isOn) {
      this.isOn = isOn;
      launchStateChangeEvent(isOn);
      makeDirty();
    }
  }
  
  private void launchStateChangeEvent(boolean isOn) {
    for (GdxStateChangeListener listener : stateChangeListeners) {
      listener.onStateChange(this, isOn);
    }
  }

  @Override
  protected void onReleased(float x, float y, int pointer) {
    if (!isDragged()) {
      isOn = !isOn;
      launchStateChangeEvent(isOn);
    }
    makeDirty();
  }
}
