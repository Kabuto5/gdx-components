package components.abstracts;

import components.aggregated.GdxListeners;
import components.listeners.GdxPositionChangeListener;

public abstract class GdxAbstractSlider extends GdxAbstractComponent {
  public enum Orientation { HORIZONTAL, VERTICAL }
  
  public static final Orientation ORIENTATION_HORIZONTAL = Orientation.HORIZONTAL;
  public static final Orientation ORIENTATION_VERTICAL = Orientation.VERTICAL;
  private static final float DEFAULT_SPEED = 1;

  private final GdxListeners<GdxPositionChangeListener> positionChangeListeners = 
      new GdxListeners<GdxPositionChangeListener>();
  private final boolean vertical;
  private float gripSize, length;
  private float minimum, maximum;
  private float speed = DEFAULT_SPEED;
  private float position = 0, targetPosition = 0;
  private float overshoot = 0;
  
  public GdxAbstractSlider(float x, float y, float width, float height, float gripSize,
      float minimum, float maximum, Orientation orientation) {
    this(x, y, width, height, gripSize, minimum, maximum, orientation == Orientation.VERTICAL);
  }
  
  public GdxAbstractSlider(float x, float y, float width, float height, float gripSize,
      float minimum, float maximum, boolean vertical) {
    super(x, y, width, height);
    makeActive();
    this.vertical = vertical;
    this.gripSize = gripSize;
    updateLength();
    this.minimum = minimum;
    this.maximum = maximum;
  }
  
  protected void addPositionChangeListener(Object tag, GdxPositionChangeListener listener) {
    positionChangeListeners.add(tag, listener);
  }
  
  protected boolean removePositionChangeListener(GdxPositionChangeListener listener) {
    return positionChangeListeners.remove(listener);
  }
  
  protected GdxPositionChangeListener removePositionChangeListener(Object tag) {
    return positionChangeListeners.remove(tag);
  }
  
  @Override
  protected void resized() {
    super.resized();
    updateLength();
  }

  protected float getMinimum() {
    return minimum;
  }

  protected void setMinimum(float minimum) {
    this.minimum = minimum;
  }

  protected float getMaximum() {
    return maximum;
  }

  protected void setMaximum(float maximum) {
    this.maximum = maximum;
  }
  
  protected float getSpeed() {
    return speed;
  }
  
  protected void setSpeed(float speed) {
    if (speed <= 0)
      throw new IllegalArgumentException("Speed must be a positive value.");
    this.speed = speed;
  }
  
  protected float getSpeedAbsolute() {
    return length * speed;
  }
  
  protected void setSpeedAbsolute(float speedAbsolute) {
    speed = speedAbsolute / length;
  }
  
  protected float getValue() {
    return minimum + (maximum - minimum) * position / length;
  }
  
  protected void setValue(float value) {
    setPosition(length * (value - minimum) / (maximum - minimum));
  }

  protected float getPosition() {
    return position;
  }

  protected void setPosition(float position) {
    setCurrentPosition(position);
    setTargetPosition(position);
  }
  
  protected float getTargetPosition() {
    return targetPosition;
  }
  
  protected void setTargetPosition(float targetPosition) {
    if (targetPosition < 0) {
      targetPosition = 0;
    }
    else if (targetPosition > length) {
      targetPosition = length;
    }
    this.targetPosition = targetPosition;
  }
  
  protected float getCurrentPosition() {
    return position;
  }

  protected void setCurrentPosition(float position) {
    if (position < 0) {
      position = 0;
    }
    else if (position > length) {
      position = length;
    }
    this.position = position;
    launchPositionChangeEvent(getValue());
  }
  
  protected float getGripSize() {
    return gripSize;
  }

  protected void setGripSize(float gripSize) {
    this.gripSize = gripSize;
    updateLength();
  }

  protected float getLength() {
    return length;
  }
  
  private void updateLength() {
    if (vertical) {
      length = getHeight() - gripSize;
    }
    else {
      length = getWidth() - gripSize;
    }
    if (position > length) position = length;
  }
  
  protected Orientation getOrientation() {
    return vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL;
  }
  
  protected boolean isVertical() {
    return vertical;
  }
  
  private void launchPositionChangeEvent(float position) {
    for (GdxPositionChangeListener listener : positionChangeListeners) {
      listener.onPositionChange(this, position);
    }
  }
  
  @Override
  public void step(float delay) {
    if (position != targetPosition) {
      if (position < targetPosition) {
        position += getSpeedAbsolute() * delay;
        if (position > targetPosition) position = targetPosition;
        setCurrentPosition(position);
      }
      else {
        position -= getSpeedAbsolute() * delay;
        if (position < targetPosition) position = targetPosition;
        setCurrentPosition(position);
      }
      makeDirty();
    }
  }

  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    boolean handled = super.onTouchDown(x, y, pointer);
    float interactiveAreaExtension = getInteractiveAreaExtension();
    float pointerPosition = vertical ? y : x;
    if (pointerPosition < position - interactiveAreaExtension
        || pointerPosition > position + gripSize + interactiveAreaExtension) {
      setTargetPosition(pointerPosition - gripSize * 0.5f);
      handled = true;
    }
    makeDirty();
    return handled;
  }

  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    super.onTouchUp(x, y, pointer);
    targetPosition = position;
    if (!isDragged()) overshoot = 0;
    makeDirty();
    return true;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    super.onDrag(x, y, differenceX, differenceY, pointer);
    float difference = vertical ? differenceY : differenceX;
    float desiredPosition = position + difference + overshoot;
    if (targetPosition == position) {
      setPosition(desiredPosition);
    }
    else {
      targetPosition += difference;
      setCurrentPosition(desiredPosition);
    }
    overshoot = desiredPosition - position;
    makeDirty();
    return true;
  }
}
