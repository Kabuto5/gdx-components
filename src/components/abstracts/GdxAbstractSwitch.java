package components.abstracts;

import com.badlogic.gdx.Gdx;
import components.aggregated.GdxListeners;
import components.listeners.GdxStateChangeListener;

/**
 * Switch that can be switched on or off by dragging between its two uttermost positions.
 */
public abstract class GdxAbstractSwitch extends GdxAbstractComponent {
  public enum Orientation { HORIZONTAL, VERTICAL }
  public enum Direction { STANDARD, REVERSED }
  public enum InitialStatus { ON, OFF }
  
  public static final Orientation ORIENTATION_HORIZONTAL = Orientation.HORIZONTAL;
  public static final Orientation ORIENTATION_VERTICAL = Orientation.VERTICAL;
  public static final Direction DIRECTION_STANDARD = Direction.STANDARD;
  public static final Direction DIRECTION_REVERSED = Direction.REVERSED;
  public static final InitialStatus START_ON = InitialStatus.ON;
  public static final InitialStatus START_OFF = InitialStatus.OFF;
  private static final float DEFAULT_ACCELERATION = 2.5f;
  
  private final GdxListeners<GdxStateChangeListener> stateChangeListeners = 
      new GdxListeners<GdxStateChangeListener>();
  private final boolean vertical;
  private final boolean reversed;
  private float gripSize, length;
  private float acceleration = DEFAULT_ACCELERATION;
  private float position = 0, targetPosition = 0;
  private float overshoot = 0;
  private float velocity = 0;
  private boolean isOn;

  public GdxAbstractSwitch(float x, float y, float width, float height, float gripSize, 
      Orientation orientation, Direction direction, InitialStatus initialStatus) {
    this(x, y, width, height, gripSize, orientation == Orientation.VERTICAL, 
        direction == Direction.REVERSED, initialStatus == InitialStatus.ON);
  }
  
  /**
   * @param x X-coordinate of a switch within its container
   * @param y Y-coordinate of a switch within its container
   * @param width Width of the whole area in which switch can move
   * @param height Height of the whole area in which switch can move
   * @param margin Outreach of switch's interactive area out of its visible boundaries
   * @param gripSize Space taken by the switch itself inside the whole area
   * @param vertical Makes switch move vertically instead of horizontally
   * @param reversed Reverses a switch so it's switched on when it's down or on the right side
   * @param startOn Makes switch to be switched on initially
   */
  public GdxAbstractSwitch(float x, float y, float width, float height, float gripSize, 
      boolean vertical, boolean reversed, boolean startOn) {
    super(x, y, width, height);
    makeActive();
    this.vertical = vertical;
    this.reversed = reversed;
    this.gripSize = gripSize;
    updateLength();
    isOn = startOn = (!reversed && startOn) || (reversed && !startOn);
    if (!startOn) setPosition(length);
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
  
  @Override
  protected void resized() {
    super.resized();
    updateLength();
  }
  
  protected Orientation getOrientation() {
    return vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL;
  }
  
  protected boolean isVertical() {
    return vertical;
  }
  
  protected Direction getDirection() {
    return reversed ? Direction.REVERSED : Direction.STANDARD;
  }
  
  protected boolean isReversed() {
    return reversed;
  }
  
  protected float getPosition() {
    return position;
  }

  protected void setPosition(float position) {
    setCurrentPosition(position);
    setTargetPosition(position);
  }
  
  protected void updatePosition(float delay) {
    setPosition(position + velocity * delay);
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
    if (position <= 0) {
      position = 0;
      switchedOn();
    }
    else if (position >= length) {
      position = length;
      switchedOff();
    }
    this.position = position;
  }
  
  protected void updateCurrentPosition(float delay) {
    setCurrentPosition(position + velocity * delay);
  }
  
  protected boolean isOn() {
    return (!reversed && isOn) || (reversed && !isOn);
  }
  
  /**
   * Immediately sets the switch into on or off position.
   * If state change is involved, listeners are invoked.
   * @param isOn Whether the switch will be on or off
   */
  protected void setOn(boolean isOn) {
    boolean newState = (!reversed && isOn) || (reversed && !isOn);
    if (this.isOn != newState) {
      setPosition(this.isOn ? 0 : getLength());
      launchStateChangeEvent(isOn);
      makeDirty();
    }
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
  
  /**
   * Acceleration determines how quickly switch speeds
   * up or slows down if left loose outside its uttermost
   * positions, until it reaches one.
   * <p>
   * The value indicates how many times per second switch
   * could theoretically move from one uttermost position
   * to the other.
   * @return Current acceleration of the switch
   */
  protected float getAcceleration() {
    return acceleration;
  }
  
  /**
   * Acceleration determines how quickly switch speeds
   * up or slows down if left loose outside its uttermost
   * positions, until it reaches one.
   * <p>
   * The value indicates how many times per second switch
   * could theoretically move from one uttermost position
   * to the other.
   * @param acceleration Acceleration of the switch
   */
  protected void setAcceleration(float acceleration) {
    if (acceleration <= 0)
      throw new IllegalArgumentException("Acceleration must be a positive value.");
    this.acceleration = acceleration;
  }
  
  /**
   * Acceleration determines how quickly switch speeds
   * up or slows down if left loose outside its uttermost
   * positions, until it reaches one.
   * <p>
   * The value indicates distance in internal units switch
   * can travel per second.
   * @return Current acceleration of the switch
   */
  protected float getAccelerationAbsolute() {
    return length * acceleration;
  }
  
  /**
   * Acceleration determines how quickly switch speeds
   * up or slows down if left loose outside its uttermost
   * positions, until it reaches one.
   * <p>
   * The value indicates distance in internal units switch
   * can travel per second.
   * @param accelerationAbsolute Acceleration of the switch
   */
  protected void setAccelerationAbsolute(float accelerationAbsolute) {
    acceleration = accelerationAbsolute / length;
  }

  protected float getVelocity() {
    return velocity;
  }

  protected void setVelocity(float velocity) {
    this.velocity = velocity;
  }
  
  /**
   * Increases velocity according to set acceleration
   * and given time delay.
   * @param delay Time delay since last update
   */
  protected void increaseVelocity(float delay) {
    velocity += getAccelerationAbsolute() * delay;
  }
  
  /**
   * Decreases velocity according to set acceleration
   * and given time delay.
   * @param delay Time delay since last update
   */
  protected void decreaseVelocity(float delay) {
    velocity -= getAccelerationAbsolute() * delay;
  }

  /**
   * Visually moves the switch into it's on position during a certain amount of time.
   * If state change is involved, listeners are invoked immediately.
   */
  protected void switchOn() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (reversed) {
          switchedOff();
          setTargetPosition(getLength());
        }
        else {
          switchedOn();
          setTargetPosition(0);
        }
        makeDirty();
      }
    });
  }
  
  /**
   * @deprecated Bad naming. Use {@link #switchOn()} instead.
   */
  @Deprecated
  protected void triggerOn() {
    switchOn();
  }
  
  /**
   * Visually moves the switch into it's off position during a certain amount of time.
   * If state change is involved, listeners are invoked immediately.
   */
  protected void switchOff() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (reversed) {
          switchedOn();
          setTargetPosition(0);
        }
        else {
          switchedOff();
          setTargetPosition(getLength());
        }
        makeDirty();
      }
    });
  }
  
  /**
   * @deprecated Bad naming. Use {@link #switchOff()} instead.
   */
  @Deprecated
  protected void triggerOff() {
    switchOff();
  }
  
  protected void switchedOff() {
    if (isOn) {
      isOn = false;
      launchStateChangeEvent(reversed);
    }
  }
  
  protected void switchedOn() {
    if (!isOn) {
      isOn = true;
      launchStateChangeEvent(!reversed);
    }
  }
  
  @Override
  public boolean insideActiveArea(float x, float y) {
    float extension = getInteractiveAreaExtension();
    if (vertical) {
      x += extension;
      y += extension - position;
      float activeHeight = getHeight() - length;
      return x > 0 && y > 0
          && x < getWidth() + 2 * extension 
          && y < activeHeight + 2 * extension;
    }
    else {
      x += extension - position;
      y += extension;
      float activeWidth = getWidth() - length;
      return x > 0 && y > 0
          && x < activeWidth + 2 * extension
          && y < getHeight() + 2 * extension;
    }
  }
  
  private void launchStateChangeEvent(boolean isOn) {
    for (GdxStateChangeListener listener : stateChangeListeners) {
      listener.onStateChange(this, isOn);
    }
  }
  
  @Override
  public void step(float delay) {
    if (!isDragged()) {
      if (position != targetPosition) {
        if (targetPosition > position) {
          increaseVelocity(delay);
        } else {
          decreaseVelocity(delay);
        }
        updateCurrentPosition(delay);
        makeDirty();
      } else if (position > 0 && position < length) {
        if (position > length * 0.5) {
          increaseVelocity(delay);
        } else {
          decreaseVelocity(delay);
        }
        updatePosition(delay);
        makeDirty();
      } else {
        velocity = 0;
      }
    }
  }

  @Override
  protected void onStopDrag(float x, float y, int pointer) {
    overshoot = 0;
    makeDirty();
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    super.onDrag(x, y, differenceX, differenceY, pointer);
    float difference = isVertical() ? differenceY : differenceX;
    float desiredPosition = position + difference + overshoot;
    setPosition(desiredPosition);
    overshoot = desiredPosition - position;
    makeDirty();
    return true;
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    velocity = isVertical() ? velocityY : velocityX;
    makeDirty();
    return true;
  }
}
