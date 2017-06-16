package components.abstracts;

import io.GdxPainter;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

import components.aggregated.GdxContent;
import components.interfaces.GdxComponent;

public class GdxAbstractScrollView extends GdxAbstractContainer {
  private static final float DEFAULT_ACCELERATION = 2.5f;
  
  private final GdxContent content = new GdxContent(this);
  private final BitSet capturedDraggingPointers = new BitSet(10);
  private int numberOfCapturedDrags = 0;
  private float accelerationX = DEFAULT_ACCELERATION;
  private float accelerationY = DEFAULT_ACCELERATION;
  private float scrollX, scrollY;
  private float overshootX = 0, overshootY = 0;
  private float velocityX = 0, velocityY = 0;
  
  public GdxAbstractScrollView(float x, float y, float width, float height, GdxComponent content) {
    super(x, y, width, height);
    makeActive();
    this.content.set(content);
  }
  
  protected float getScrollX() {
    return scrollX;
  }
  
  protected float getScrollY() {
    return scrollY;
  }
  
  protected void setScrollX(float scrollX) {
    this.scrollX = scrollX;
    velocityX = 0;
    velocityY = 0;
    updatePosition();
  }
  
  protected void setScrollY(float scrollY) {
    this.scrollY = scrollY;
    velocityX = 0;
    velocityY = 0;
    updatePosition();
  }
  
  protected void setScroll(float scrollX, float scrollY) {
    this.scrollX = scrollX;
    this.scrollY = scrollY;
    velocityX = 0;
    velocityY = 0;
    updatePosition();
  }
  
  protected float trimScrollX(float scrollX) {
    if (scrollX < 0) return 0;
    float maxScrollX = content.get().getWidth() - getWidth();
    if (maxScrollX > 0) {
      if (scrollX > maxScrollX) return maxScrollX;
      return scrollX;
    } else {
      return 0;
    }
  }
  
  protected float trimScrollY(float scrollY) {
    if (scrollY < 0) return 0;
    float maxScrollY = content.get().getHeight() - getHeight();
    if (maxScrollY > 0) {
      if (scrollY > maxScrollY) return maxScrollY;
      return scrollY;
    } else {
      return 0;
    }
  }
  
  protected void updatePosition() {
    scrollX = trimScrollX(scrollX);
    scrollY = trimScrollY(scrollY);
    content.get().setLocation(- scrollX, - scrollY);
  }
  
  protected GdxComponent getContent() {
    return content.get();
  }
  
  protected void setContent(GdxComponent component) {
    content.set(component);
    updatePosition();
  }

  @Override
  public Collection<GdxComponent> getComponents() {
    return content.collection();
  }

  @Override
  public boolean hasComponent(GdxComponent component) {
    return content.is(component);
  }

  @Override
  public boolean removeComponent(GdxComponent component) {
    return content.remove(component);
  }

  @Override
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y) {
    return content.collection().iterator();
  }

  @Override
  protected void resized() {
    super.resized();
    updatePosition();
  }

  @Override
  public void reportResize(GdxComponent component) {
    updatePosition();
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates how many times per second it can
   * move by a distance equal to its width.
   * @return Current acceleration of the ScrollView on 
   *         the horizontal axis
   */
  protected float getAccelerationX() {
    return accelerationX;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates how many times per second it can
   * move by a distance equal to its height.
   * @return Current acceleration of the ScrollView on
   *         the vertical axis.
   */
  protected float getAccelerationY() {
    return accelerationY;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates how many times per second it can
   * move by a distance equal to its width.
   * @param accelerationX Acceleration of the ScrollView on
   *        the horizontal axis.
   */
  protected void setAccelerationX(float accelerationX) {
    if (accelerationX <= 0)
      throw new IllegalArgumentException("Acceleration must be a positive value.");
    this.accelerationX = accelerationX;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates how many times per second it can
   * move by a distance equal to its height.
   * @param accelerationY Acceleration of the ScrollView on
   *        the vertical axis.
   */
  protected void setAccelerationY(float accelerationY) {
    if (accelerationY <= 0)
      throw new IllegalArgumentException("Acceleration must be a positive value.");
    this.accelerationY = accelerationY;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates by how many internal units ScrollView
   * can scroll per second on the horizontal axis.
   * @return Current acceleration of the ScrollView on 
   *         the horizontal axis
   */
  protected float getAccelerationAbsoluteX() {
    return getWidth() * accelerationX;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates by how many internal units ScrollView
   * can scroll per second on the vertical axis.
   * @return Current acceleration of the ScrollView on 
   *         the vertical axis
   */
  protected float getAccelerationAbsoluteY() {
    return getHeight() * accelerationY;
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates by how many internal units ScrollView
   * can scroll per second on horizontal axis.
   * @param accelerationAbsoluteX Acceleration of the ScrollView on
   *        the horizontal axis.
   */
  protected void setAccelerationAbsoluteX(float accelerationAbsoluteX) {
    if (accelerationAbsoluteX <= 0)
      throw new IllegalArgumentException("Acceleration must be a positive value.");
    accelerationX = accelerationAbsoluteX / getWidth();
  }
  
  /**
   * Acceleration determines how quickly ScrollView slows
   * down after it was set in motion.
   * <p>
   * The value indicates by how many internal units ScrollView
   * can scroll per second on vertical axis.
   * @param accelerationAbsoluteY Acceleration of the ScrollView on
   *        the vertical axis.
   */
  protected void setAccelerationAbsoluteY(float accelerationAbsoluteY) {
    if (accelerationAbsoluteY <= 0)
      throw new IllegalArgumentException("Acceleration must be a positive value.");
    accelerationY = accelerationAbsoluteY / getHeight();
  }
  
  protected float getVelocityX() {
    return velocityX;
  }
  
  protected float getVelocityY() {
    return velocityY;
  }

  protected void setVelocityX(float velocityX) {
    this.velocityX = velocityX;
  }

  protected void setVelocityY(float velocityY) {
    this.velocityY = velocityY;
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if (painter.pushClippingArea(x, y, getWidth(), getHeight())) {
      GdxComponent component = content.get();
      painter.paintComponent(x + component.getX(), y + component.getY(), component);
      painter.popClippingArea();
    }
  }

  @Override
  public boolean isDragged() {
    return super.isDragged() || numberOfCapturedDrags > 0;
  }

  @Override
  public int getNumberOfDragging() {
    return super.getNumberOfDragging() + numberOfCapturedDrags;
  }
  
  @Override
  public void step(float delay) {
    if (!isDragged()) {
      boolean doScroll = false;
      if (velocityX != 0) {
        if (velocityX > 0) {
          velocityX -= getAccelerationAbsoluteX() * delay;
          if (velocityX < 0) {
            velocityX = 0;
          } else {
            doScroll = true;
          }
        } else {
          velocityX += getAccelerationAbsoluteX() * delay;
          if (velocityX > 0) {
            velocityX = 0;
          } else {
            doScroll = true;
          }
        }
      }
      if (velocityY != 0) {
        if (velocityY > 0) {
          velocityY -= getAccelerationAbsoluteY() * delay;
          if (velocityY < 0) {
            velocityY = 0;
          } else {
            doScroll = true;
          }
        } else {
          velocityY += getAccelerationAbsoluteY() * delay;
          if (velocityY > 0) {
            velocityY = 0;
          } else {
            doScroll = true;
          }
        }
      }
      if (doScroll) {
        float previousScrollX = scrollX;
        float previousScrollY = scrollY;
        scrollX -= velocityX * delay;
        scrollY -= velocityY * delay;
        updatePosition();
        if (previousScrollX == scrollX) velocityX = 0;
        if (previousScrollY == scrollY) velocityY = 0;
        makeDirty();
      }
    }
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    super.onDrag(x, y, differenceX, differenceY, pointer);
    float desiredScrollX = scrollX - differenceX + overshootX;
    float desiredScrollY = scrollY - differenceY + overshootY;
    scrollX = desiredScrollX;
    scrollY = desiredScrollY;
    updatePosition();
    overshootX += desiredScrollX - scrollX;
    overshootY += desiredScrollY - scrollY;
    return true;
  }

  @Override
  protected void onStopDrag(float x, float y, int pointer) {
    overshootX = 0;
    overshootY = 0;
    super.onStopDrag(x, y, pointer);
  }

  @Override
  public boolean onDragReceived(float x, float y, float differenceX, float differenceY, int pointer) {
    super.onDragReceived(x, y, differenceX, differenceY, pointer);
    if (!capturedDraggingPointers.get(pointer)) {
      capturedDraggingPointers.set(pointer, true);
      numberOfCapturedDrags++;
      if (getNumberOfDragging() == 1) onStartDrag(x, y, pointer);
    }
    return true;
  }

  @Override
  public void onDragCapturingStopped(float x, float y, int pointer) {
    super.onDragCapturingStopped(x, y, pointer);
    capturedDraggingPointers.set(pointer, false);
    numberOfCapturedDrags--;
    if (getNumberOfDragging() == 0) onStopDrag(x, y, pointer);
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    makeDirty();
    return true;
  }

  @Override
  public void dispose() {
    content.dispose();
    super.dispose();
  }
}
