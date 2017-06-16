package components;

import helpers.TextureUtils;
import io.GdxPainter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import components.abstracts.GdxAbstractSwitch;
import components.listeners.GdxStateChangeListener;

public class GdxSwitch extends GdxAbstractSwitch {
  private final TextureRegion image, activeImage;
  
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      Orientation orientation, Direction direction, InitialStatus initialStatus) {
    super(x, y, width, height, orientation == Orientation.VERTICAL ? TextureUtils.scaleHeight(width, image) 
        : TextureUtils.scaleWidth(height, image), orientation, direction, initialStatus);
    this.image = image;
    this.activeImage = activeImage;
  }
  
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      Orientation orientation, InitialStatus initialStatus) {
    this(image, activeImage, x, y, width, height, orientation, Direction.STANDARD, initialStatus);
  }
  
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      Orientation orientation) {
    this(image, activeImage, x, y, width, height, orientation, Direction.STANDARD, InitialStatus.OFF);
  }
  
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height, 
      Orientation orientation, Direction direction, InitialStatus initialStatus) {
    this(image, image, x, y, width, height, orientation, direction, initialStatus);
  }
  
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height,
      Orientation orientation, InitialStatus initialStatus) {
    this(image, image, x, y, width, height, orientation, initialStatus);
  }
  
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height, 
      Orientation orientation) {
    this(image, image, x, y, width, height, orientation);
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      boolean vertical, boolean reversed, boolean startOn) {
    super(x, y, width, height, vertical ? TextureUtils.scaleHeight(width, image) 
        : TextureUtils.scaleWidth(height, image), vertical, reversed, startOn);
    this.image = image;
    this.activeImage = activeImage;
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      boolean vertical, boolean startOn) {
    this(image, activeImage, x, y, width, height, vertical, false, startOn);
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, TextureRegion activeImage, float x, float y, float width, float height,
      boolean vertical) {
    this(image, activeImage, x, y, width, height, vertical, false, false);
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height,
      boolean vertical, boolean reversed, boolean startOn) {
    this(image, image, x, y, width, height, vertical, reversed, startOn);
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height,
      boolean vertical, boolean startOn) {
    this(image, image, x, y, width, height, vertical, startOn);
  }

  @Deprecated
  public GdxSwitch(TextureRegion image, float x, float y, float width, float height,
      boolean vertical) {
    this(image, image, x, y, width, height, vertical);
  }
  
  @Override
  public void addStateChangeListener(Object tag, GdxStateChangeListener listener) {
    super.addStateChangeListener(tag, listener);
  }

  @Override
  public boolean removeStateChangeListener(GdxStateChangeListener listener) {
    return super.removeStateChangeListener(listener);
  }

  @Override
  public GdxStateChangeListener removeStateChangeListener(Object tag) {
    return super.removeStateChangeListener(tag);
  }

  @Override
  protected Orientation getOrientation() {
    return super.getOrientation();
  }

  @Override
  public boolean isVertical() {
    return super.isVertical();
  }

  @Override
  protected Direction getDirection() {
    return super.getDirection();
  }

  @Override
  public boolean isReversed() {
    return super.isReversed();
  }

  @Override
  public boolean isOn() {
    return super.isOn();
  }

  @Override
  public void setOn(boolean isOn) {
    super.setOn(isOn);
  }

  @Override
  public float getAcceleration() {
    return super.getAcceleration();
  }

  @Override
  public void setAcceleration(float acceleration) {
    super.setAcceleration(acceleration);
  }

  @Override
  public float getAccelerationAbsolute() {
    return super.getAccelerationAbsolute();
  }

  @Override
  public void setAccelerationAbsolute(float accelerationAbsolute) {
    super.setAccelerationAbsolute(accelerationAbsolute);
  }

  @Override
  public void switchOn() {
    super.switchOn();
  }

  @Deprecated
  @Override
  public void triggerOn() {
    switchOn();
  }

  @Override
  public void switchOff() {
    super.switchOff();
  }

  @Deprecated
  @Override
  public void triggerOff() {
    switchOff();
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if (isVertical()) {
      if (isDragged())
        painter.draw(activeImage, x, y + getPosition(), getWidth(), getGripSize());
      else {
        painter.draw(image, x, y + getPosition(), getWidth(), getGripSize());
      }
    }
    else {
      if (isDragged()) {
        painter.draw(activeImage, x + getPosition(), y, getGripSize(), getHeight());
      }
      else {
        painter.draw(image, x + getPosition(), y, getGripSize(), getHeight());
      }
    }
  }
}
