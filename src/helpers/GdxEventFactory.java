package helpers;

import components.interfaces.GdxComponent;
import components.interfaces.GdxComponent.GdxEvent;
import components.listeners.GdxComponentListener;

public class GdxEventFactory {
  public static GdxEvent createEvent(Object type, Object data) {
    return new GdxEvent(type, data);
  }
  
  public static IllegalArgumentException createListenerException(GdxComponent component, GdxComponentListener listener, Class<? extends GdxComponentListener> expectedClass) {
    return new IllegalArgumentException(
        String.format("Listener passed into %s had been expected to be an instance of %s, but was instance of %s",
            component.getClass().getCanonicalName(), expectedClass.getCanonicalName(), listener.getClass().getCanonicalName()));
  }
}
