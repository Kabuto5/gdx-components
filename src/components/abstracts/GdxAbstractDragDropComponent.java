package components.abstracts;

import helpers.ComponentUtils;
import io.GdxPainter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import components.GdxMainFrame;
import components.aggregated.GdxListeners;
import components.interfaces.GdxComponent;
import components.interfaces.GdxDragDropComponent;
import components.interfaces.GdxDragTarget;
import components.listeners.GdxDragDropListener;

/**
 * This implementation of the {@link GdxDragDropComponent} interface provides essential 
 * functionality for any standard draggable component, along with some default behavior.
 */
public abstract class GdxAbstractDragDropComponent extends GdxAbstractComponent implements GdxDragDropComponent {
  private final GdxListeners<GdxDragDropListener> dragDropListeners = 
      new GdxListeners<GdxDragDropListener>();
  private float dragX = Float.NaN, dragY = Float.NaN;
  private GdxComponent currentDragTarget = null;
  
  public GdxAbstractDragDropComponent(float x, float y, float width, float height) {
    super(x, y, width, height);
    makeActive();
  }
  
  protected void addDragDropListener(Object tag, GdxDragDropListener listener) {
    dragDropListeners.add(tag, listener);
  }
  
  protected boolean removeDragDropListener(GdxDragDropListener listener) {
    return dragDropListeners.remove(listener);
  }
  
  protected GdxDragDropListener removeDragDropListener(Object tag) {
    return dragDropListeners.remove(tag);
  }
  
  @Override
  public boolean isAccepted() {
    return currentDragTarget != null;
  }
  
  @Override
  public float getDragX() {
    return Float.isNaN(dragX) ? getFrameX() : dragX;
  }
  
  @Override
  public float getDragY() {
    return Float.isNaN(dragY) ? getFrameY() : dragY;
  }
  
  /**
   * Returns an x-coordinate of location where component is currently to be dropped,
   * in relation to {@link GdxMainFrame frame}.
   * <p>
   * By default, the drop location is equal to the current coordinates of the component's
   * center. This method can be overriden in order to change drop location.
   * @return Current x-coordinate of component's drop location.
   */
  public float getDropX() {
    return getDragX() + getWidth() * 0.5f;
  }
  
  /**
   * Returns an y-coordinate of location where component is currently to be dropped,
   * in relation to {@link GdxMainFrame frame}.
   * <p>
   * By default, the drop location is equal to the current coordinates of the component's
   * center. This method can be overriden in order to change drop location.
   * @return Current y-coordinate of component's drop location.
   */
  public float getDropY() {
    return getDragY() + getHeight() * 0.5f;
  }
  
  private void launchDragStartEvent() {
    for (GdxDragDropListener listener : dragDropListeners) {
      listener.onStartDrag(this);
    }
  }
  
  private void launchDragAbortEvent() {
    for (GdxDragDropListener listener : dragDropListeners) {
      listener.onDragAbort(this);
    }
  }
  
  private void launchDragDropEvent(GdxComponent target, float dropX, float dropY) {
    for (GdxDragDropListener listener : dragDropListeners) {
      listener.onDragDrop(this, target, dropX, dropY);
    }
  }
  
  /**
   * Paints a representation of the component while it's dragged.
   * By default, this paints half transparent variant of the 
   * component's regular representation. The result is not guaranteed
   * since the behavior can be overriden by regular 
   * {@link #paint(float, float, GdxPainter) paint} method.
   * @param x Coordinate relative to component's frame
   * @param y Coordinate relative to component's frame
   * @param painter Provides access to drawing operations
   */
  @Override
  public void dragPaint(float x, float y, GdxPainter painter) {
    Color color = painter.getColor();
    painter.setColor(color.r, color.g, color.b, color.a * 0.5f);
    paint(x, y, painter);
    painter.setColor(color);
  }
  
  private void updateDragTarget() {
    GdxComponent dragTarget = ComponentUtils.findDragTarget(this, getDropX(), getDropY());
    if (dragTarget != currentDragTarget) {
      if (currentDragTarget != null) {
        ((GdxDragTarget)currentDragTarget).onDragOut(this);
      }
      currentDragTarget = dragTarget;
      if (currentDragTarget != null) {
        ((GdxDragTarget)currentDragTarget).onDragOver(this);
      }
    }
  }
  
  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    super.onDrag(x, y, differenceX, differenceY, pointer);
    dragX += differenceX;
    dragY += differenceY;
    updateDragTarget();
    return true;
  }

  @Override
  protected void onStartDrag(float x, float y, int pointer) {
    GdxMainFrame frame = getFrame();
    if (frame == null) throw new IllegalStateException("Component is not in frame.");
    launchDragStartEvent();
    frame.dragStart(this);
    dragX = getFrameX();
    dragY = getFrameY();
    updateDragTarget();
  }

  @Override
  protected void onStopDrag(float x, float y, int pointer) {
    if (currentDragTarget != null) {
      Vector2 targetFrameLocation = currentDragTarget.getFrameLocation();
      float dropX = getDropX() - targetFrameLocation.x;
      float dropY = getDropY() - targetFrameLocation.y;
      launchDragDropEvent(currentDragTarget, dropX, dropY);
      ((GdxDragTarget)currentDragTarget).onDragDrop(this, dropX, dropY);
      currentDragTarget = null;
    }
    else {
      launchDragAbortEvent();
    }
    dragX = Float.NaN;
    dragY = Float.NaN;
    GdxMainFrame frame = getFrame();
    if (frame == null) throw new IllegalStateException("Component is not in frame.");
    frame.dragStop(this);
  }
}
