package components;

import io.GdxPainter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import components.abstracts.GdxAbstractComponent;

public class GdxImage extends GdxAbstractComponent {
  private TextureRegion image;
  
  public GdxImage(TextureRegion image, float x, float y, float width, float height) {
    super(x, y, width, height);
    this.image = image;
  }

  public TextureRegion getImage() {
    return image;
  }

  public void setImage(TextureRegion image) {
    this.image = image;
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    painter.draw(image, x, y, getWidth(), getHeight());
  }
}
