package components;

import io.GdxPainter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import components.abstracts.GdxAbstractToggleButton;
import components.listeners.GdxStateChangeListener;

public class GdxToggleButton extends GdxAbstractToggleButton {
  private final TextureRegion image, activeImage;
  
  public GdxToggleButton(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height, boolean startOn) {
    super(x, y, width, height, startOn);
    this.image = image;
    this.activeImage = activeImage;
  }

  public GdxToggleButton(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height) {
    this(image, activeImage, x, y, width, height, false);
  }
  
  public GdxToggleButton(TextureRegion image, float x, float y, float width, float height,
      boolean startOn) {
    this(image, image, x, y, width, height, startOn);
  }

  public GdxToggleButton(TextureRegion image, float x, float y, float width, float height) {
    this(image, image, x, y, width, height);
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
  public boolean isOn() {
    return super.isOn();
  }

  @Override
  public void setOn(boolean isOn) {
    super.setOn(isOn);
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if ((isOn() && !isPressed()) || (!isOn() && isPressed()))
      painter.draw(activeImage, x, y, getWidth(), getHeight());
    else
      painter.draw(image, x, y, getWidth(), getHeight());
  }
}
