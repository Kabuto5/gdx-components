package components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GdxReturningSwitch extends GdxSwitch {
  public GdxReturningSwitch(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, Orientation orientation, Direction direction) {
    super(image, activeImage, x, y, width, height, orientation, direction, InitialStatus.OFF);
  }
  
  public GdxReturningSwitch(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, Orientation orientation) {
    this(image, activeImage, x, y, width, height, orientation, Direction.STANDARD);
  }
  
  public GdxReturningSwitch(TextureRegion image, float x, float y, float width, float height,
      Orientation orientation, Direction direction) {
    this(image, image, x, y, width, height, orientation, direction);
  }
  
  public GdxReturningSwitch(TextureRegion image, float x, float y, float width, float height,
      Orientation orientation) {
    this(image, image, x, y, width, height, orientation);
  }
  
  @Deprecated
  public GdxReturningSwitch(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, boolean vertical, boolean reversed) {
    super(image, activeImage, x, y, width, height, vertical, reversed, false);
  }

  @Deprecated
  public GdxReturningSwitch(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, boolean vertical) {
    this(image, activeImage, x, y, width, height, vertical, false);
  }

  @Deprecated
  public GdxReturningSwitch(TextureRegion image, float x, float y, float width, float height,
      boolean vertical, boolean reversed) {
    this(image, image, x, y, width, height, vertical, reversed);
  }

  @Deprecated
  public GdxReturningSwitch(TextureRegion image, float x, float y, float width, float height,
      boolean vertical) {
    this(image, image, x, y, width, height, vertical);
  }
  
  @Override
  public void setOn(boolean isOn) {
    throw new UnsupportedOperationException("Returning switch can not be switched programmatically.");
  }

  @Override
  public void switchOn() {
    throw new UnsupportedOperationException("Returning switch can not be switched programmatically.");
  }

  @Override
  public void switchOff() {
    throw new UnsupportedOperationException("Returning switch can not be switched programmatically.");
  }

  @Override
  public void step(float delay) {
    if (!isDragged()) {
      boolean reversed = isReversed();
      float position = getPosition();
      if (reversed) {
        if (position == getLength()) {
          setVelocity(0);
        }
        if (position > 0) {
          decreaseVelocity(delay);
          updateCurrentPosition(delay);
          makeDirty();
        } else {
          setVelocity(0);
        }
      } else {
        if (position == 0) {
          setVelocity(0);
        }
        if (position < getLength()) {
          increaseVelocity(delay);
          updateCurrentPosition(delay);
          makeDirty();
        } else {
          setVelocity(0);
        }
      }
    }
  }
}
