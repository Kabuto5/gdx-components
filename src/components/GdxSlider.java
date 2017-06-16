package components;

import helpers.TextureUtils;
import io.GdxPainter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import components.abstracts.GdxAbstractSlider;
import components.listeners.GdxPositionChangeListener;

public class GdxSlider extends GdxAbstractSlider {
  private final TextureRegion image, activeImage;
  
  public GdxSlider(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, float minimum, float maximum, Orientation orientation) {
    super(x, y, width, height, orientation == Orientation.VERTICAL ? 
        TextureUtils.scaleHeight(width, image) : TextureUtils.scaleWidth(height, image), 
        minimum, maximum, orientation);
    this.image = image;
    this.activeImage = activeImage;
  }
  
  public GdxSlider(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, Orientation orientation) {
    this(image, activeImage, x, y, width, height, 0, 1, orientation);
  }
  
  public GdxSlider(TextureRegion image, float x, float y, float width, float height, 
      float minimum, float maximum, Orientation orientation) {
    this(image, image, x, y, width, height, minimum, maximum, orientation);
  }
  
  public GdxSlider(TextureRegion image, float x, float y, float width, float height, 
      Orientation orientation) {
    this(image, x, y, width, height, 0, 1, orientation);
  }
  
  @Deprecated
  public GdxSlider(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, float minimum, float maximum, boolean vertical) {
    super(x, y, width, height, vertical ? TextureUtils.scaleHeight(width, image)
        : TextureUtils.scaleWidth(height, image), minimum, maximum, vertical);
    this.image = image;
    this.activeImage = activeImage;
  }

  @Deprecated
  public GdxSlider(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, boolean vertical) {
    this(image, activeImage, x, y, width, height, 0, 1, vertical);
  }

  @Deprecated
  public GdxSlider(TextureRegion image, float x, float y, float width, float height, 
      float minimum, float maximum, boolean vertical) {
    this(image, image, x, y, width, height, minimum, maximum, vertical);
  }

  @Deprecated
  public GdxSlider(TextureRegion image, float x, float y, float width, float height, 
      boolean vertical) {
    this(image, x, y, width, height, 0, 1, vertical);
  }

  @Override
  public void addPositionChangeListener(Object tag, GdxPositionChangeListener listener) {
    super.addPositionChangeListener(tag, listener);
  }

  @Override
  public boolean removePositionChangeListener(GdxPositionChangeListener listener) {
    return super.removePositionChangeListener(listener);
  }

  @Override
  public GdxPositionChangeListener removePositionChangeListener(Object tag) {
    return super.removePositionChangeListener(tag);
  }

  @Override
  public float getMinimum() {
    return super.getMinimum();
  }

  @Override
  public void setMinimum(float minimum) {
    super.setMinimum(minimum);
  }

  @Override
  public float getMaximum() {
    return super.getMaximum();
  }

  @Override
  public void setMaximum(float maximum) {
    super.setMaximum(maximum);
  }

  @Override
  public float getValue() {
    return super.getValue();
  }

  @Override
  public void setValue(float value) {
    super.setValue(value);
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
