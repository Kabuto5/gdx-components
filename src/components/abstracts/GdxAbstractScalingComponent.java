package components.abstracts;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import components.aggregated.GdxListeners;
import components.listeners.GdxScaleChangeListener;

public abstract class GdxAbstractScalingComponent extends GdxAbstractComponent {
  private static final float DEFAULT_ZOOM_SPEED = 0.5f;
  
  private final GdxListeners<GdxScaleChangeListener> scaleChangeListeners = 
      new GdxListeners<GdxScaleChangeListener>();
  private final HashMap<Integer, Vector2> pointers = new HashMap<Integer, Vector2>();
  private float minZoom, optimalZoom, maxZoom;
  private float zoomSpeed = DEFAULT_ZOOM_SPEED;
  private float zoom, targetZoom;
  private float baseZoom, baseDistance;
  private Vector2 zoomAt = new Vector2();
  private boolean doubleTapEnabled = true;
  private boolean singlePointerEnabled = false;
  
  public GdxAbstractScalingComponent(float x, float y, float width, float height,
      float minZoom, float optimalZoom, float maxZoom) {
    super(x, y, width, height);
    makeActive();
    this.minZoom = minZoom;
    this.maxZoom = maxZoom;
    setOptimalZoom(optimalZoom);
    setZoom(minZoom, zoomAt.set(width / 2, height / 2));
  }
  
  protected void addScaleChangeListener(Object tag, GdxScaleChangeListener listener) {
    scaleChangeListeners.add(tag, listener);
  }
  
  protected boolean removeScaleChangeListener(GdxScaleChangeListener listener) {
    return scaleChangeListeners.remove(listener);
  }
  
  protected GdxScaleChangeListener removeScaleChangeListener(Object tag) {
    return scaleChangeListeners.remove(tag);
  }
  
  protected float getMinZoom() {
    return minZoom;
  }

  protected void setMinZoom(float minZoom) {
    if (minZoom > maxZoom)
      throw new IllegalArgumentException(String.format("minZoom cannot be higher than maxZoom (%f)", maxZoom));
    if (optimalZoom < minZoom) optimalZoom = minZoom;
    if (targetZoom < minZoom) targetZoom = minZoom;
    if (zoom < minZoom) zoom = minZoom;
    this.minZoom = minZoom;
  }
  
  private float trimZoom(float zoom) {
    if (zoom < minZoom) return minZoom;
    if (zoom > maxZoom) return maxZoom;
    return zoom;
  }

  protected float getOptimalZoom() {
    return optimalZoom;
  }

  protected void setOptimalZoom(float optimalZoom) {
    this.optimalZoom = trimZoom(optimalZoom);
  }

  protected float getMaxZoom() {
    return maxZoom;
  }

  protected void setMaxZoom(float maxZoom) {
    if (maxZoom < minZoom)
      throw new IllegalArgumentException(String.format("maxZoom cannot be lower than minZoom (%f)", minZoom));
    if (optimalZoom > maxZoom) optimalZoom = maxZoom;
    if (targetZoom > maxZoom) targetZoom = maxZoom;
    if (zoom > maxZoom) zoom = maxZoom;
    this.maxZoom = maxZoom;
  }

  protected float getZoomSpeed() {
    return zoomSpeed;
  }

  protected void setZoomSpeed(float zoomSpeed) {
    this.zoomSpeed = zoomSpeed;
  }

  protected float getZoom() {
    return zoom;
  }
  
  protected void setZoom(float zoom, Vector2 zoomAt) {
    setCurrentZoom(zoom, zoomAt);
    launchRescaledEvent(zoom, zoomAt);
  }
  
  private void setCurrentZoom(float zoom, Vector2 zoomAt) {
    this.zoom = targetZoom = trimZoom(zoom);
    onScaleChange(zoom, zoomAt);
    launchScaleChangeEvent(zoom, zoomAt);
    makeDirty();
  }
  
  protected void doZoom(float zoom, Vector2 zoomAt) {
    this.zoomAt = zoomAt;
    baseZoom = this.zoom;
    targetZoom = trimZoom(zoom);
    makeDirty();
  }

  protected boolean isDoubleTapEnabled() {
    return doubleTapEnabled;
  }

  /**
   * Sets if user is allowed to switch between minimal and optimal zoom
   * by double tapping the component.
   * @param doubleTapEnabled Whether to enable double tap
   */
  protected void setDoubleTapEnabled(boolean doubleTapEnabled) {
    this.doubleTapEnabled = doubleTapEnabled;
  }

  protected boolean isSinglePointerEnabled() {
    return singlePointerEnabled;
  }
  
  /**
   * Sets if user is allowed to rescale component using only a single pointer.
   * In such a case, center of the component will serve as a fixed second pointer.
   * @param singlePointerEnabled Whether to enable single pointer
   */
  protected void setSinglePointerEnabled(boolean singlePointerEnabled) {
    this.singlePointerEnabled = singlePointerEnabled;
  }

  private void launchScaleChangeEvent(float zoom, Vector2 zoomAt) {
    for (GdxScaleChangeListener listener : scaleChangeListeners) {
      listener.onScaleChange(this, zoom, zoomAt);
    }
  }

  private void launchRescaledEvent(float zoom, Vector2 zoomAt) {
    for (GdxScaleChangeListener listener : scaleChangeListeners) {
      listener.onRescaled(this, zoom, zoomAt);
    }
  }

  @Override
  public void step(float delay) {
    if (zoom < targetZoom) {
      zoom += Math.abs(baseZoom - targetZoom) / zoomSpeed * delay;
      onScaleChange(zoom, zoomAt);
      launchScaleChangeEvent(zoom, zoomAt);
      if (zoom > targetZoom) {
        zoom = targetZoom;
        launchRescaledEvent(zoom, zoomAt);
      }
      else {
        makeDirty();
      }
    }
    else if (zoom > targetZoom) {
      zoom -= Math.abs(baseZoom - targetZoom) / zoomSpeed * delay;
      onScaleChange(zoom, zoomAt);
      launchScaleChangeEvent(zoom, zoomAt);
      if (zoom < targetZoom) {
        zoom = targetZoom;
        launchRescaledEvent(zoom, zoomAt);
      }
      else {
        makeDirty();
      }
    }
  }
  
  private float pointersDistance() {
    float distance = 0;
    if (pointers.size() > 1) {
      for (Vector2 coords1 : pointers.values()) {
        for (Vector2 coords2 : pointers.values()) {
          distance += Math.hypot(coords1.x - coords2.x, coords1.y - coords2.y);
        }
      }
    }
    else if (singlePointerEnabled) {
      float centerX = getWidth() * 0.5f;
      float centerY = getHeight() * 0.5f;
      for (Vector2 coords : pointers.values()) {
        distance = (float)Math.hypot(coords.x - centerX, coords.y - centerY);
      }
    }
    return distance;
  }
  
  private void pointersCenter(Vector2 center) {
    center.x = 0;
    center.y = 0;
    for (Vector2 coords : pointers.values()) {
      center.x += coords.x;
      center.y += coords.y;
    }
    center.x /= pointers.size();
    center.y /= pointers.size();
  }
  
  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    super.onTouchDown(x, y, pointer);
    pointers.put(pointer, new Vector2(x, y));
    baseZoom = targetZoom = zoom;
    baseDistance = pointersDistance();
    return true;
  }

  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    super.onTouchUp(x, y, pointer);
    pointers.remove(pointer);
    baseZoom = targetZoom = zoom;
    baseDistance = pointersDistance();
    if (pointers.size() == (singlePointerEnabled ? 0 : 1)) {
      launchRescaledEvent(zoom, zoomAt);
    }
    return true;
  }

  @Override
  public boolean onTap(float x, float y, int tapCount, int pointer) {
    super.onTap(x, y, tapCount, pointer);
    if (doubleTapEnabled && tapCount == 2) {
      doZoom(targetZoom > minZoom ? minZoom : optimalZoom, zoomAt.set(x, y));
      makeDirty();
    }
    return true;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    boolean handled = super.onDrag(x, y, differenceX, differenceY, pointer);
    Vector2 coords = pointers.get(pointer);
    if (coords != null) {
      coords.x += differenceX;
      coords.y += differenceY;
      if (singlePointerEnabled || pointers.size() > 1) {
        float distance = pointersDistance();
        if (pointers.size() > 1) {
          pointersCenter(zoomAt);
        }
        setCurrentZoom(Math.min(maxZoom, Math.max(minZoom, distance / baseDistance * baseZoom)), zoomAt);
      }
      return true;
    }
    return handled;
  }
  
  protected void onScaleChange(float zoom, Vector2 zoomAt) { }
}
