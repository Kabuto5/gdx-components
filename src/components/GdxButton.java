package components;

import io.GdxPainter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import components.abstracts.GdxAbstractButton;
import components.listeners.GdxClickListener;

public class GdxButton extends GdxAbstractButton {
  private final TextureRegion image, activeImage;
  
  public GdxButton(TextureRegion image, TextureRegion activeImage, float x, float y,
      float width, float height) {
    super(x, y, width, height);
    this.image = image;
    this.activeImage = activeImage;
  }
  
  public GdxButton(TextureRegion image, float x, float y, float width, float height) {
    this(image, image, x, y, width, height);
  }
  
  @Override
  public void addClickListener(Object tag, GdxClickListener listener) {
    super.addClickListener(tag, listener);
  }

  @Override
  public boolean removeClickListener(GdxClickListener listener) {
    return super.removeClickListener(listener);
  }

  @Override
  public GdxClickListener removeClickListener(Object tag) {
    return super.removeClickListener(tag);
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if (isPressed())
      painter.draw(activeImage, x, y, getWidth(), getHeight());
    else
      painter.draw(image, x, y, getWidth(), getHeight());
  }
}
