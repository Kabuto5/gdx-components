package components;

import io.GdxPainter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import components.abstracts.GdxAbstractScalingComponent;

public class GdxScalingImage extends GdxAbstractScalingComponent {
  private static final float MAX_PIXEL_SIZE = 16;
  private Texture image;
  private float baseScale = Float.NaN;
  
  public GdxScalingImage(Texture image, float x, float y, float width, float height) {
    super(x, y, width, height, 1, 1, 1);
    this.image = image;
  }

  public Texture getImage() {
    return image;
  }

  public void setImage(Texture image) {
    this.image = image;
  }

  @Override
  protected float getMinZoom() {
    return super.getMinZoom();
  }

  @Override
  protected float getMaxZoom() {
    return super.getMaxZoom();
  }

  @Override
  public float getZoomSpeed() {
    return super.getZoomSpeed();
  }

  @Override
  public void setZoomSpeed(float zoomSpeed) {
    super.setZoomSpeed(zoomSpeed);
  }

  @Override
  public float getZoom() {
    return super.getZoom();
  }

  @Override
  public void setZoom(float zoom, Vector2 zoomAt) {
    super.setZoom(zoom, zoomAt);
  }

  @Override
  public void doZoom(float zoom, Vector2 zoomAt) {
    super.doZoom(zoom, zoomAt);
  }

  @Override
  public boolean isDoubleTapEnabled() {
    return super.isDoubleTapEnabled();
  }

  @Override
  public void setDoubleTapEnabled(boolean doubleTapEnabled) {
    super.setDoubleTapEnabled(doubleTapEnabled);
  }

  @Override
  public boolean isSinglePointerEnabled() {
    return super.isSinglePointerEnabled();
  }

  @Override
  public void setSinglePointerEnabled(boolean singlePointerEnabled) {
    super.setSinglePointerEnabled(singlePointerEnabled);
  }

  private void updateZoomBounds(float scale) {
    baseScale = scale;
    float pixelPerfectZoom = Math.min(
        image.getWidth() * (scale / getWidth()),
        image.getHeight() * (scale / getHeight()));
    setMaxZoom(pixelPerfectZoom * MAX_PIXEL_SIZE);
    setOptimalZoom(pixelPerfectZoom);
  }
  
  @Override
  protected void resized() {
    super.resized();
    if (!Float.isNaN(baseScale)) updateZoomBounds(baseScale);
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if (baseScale != painter.getCanvasUnitsPerPixel()) {
      updateZoomBounds(painter.getCanvasUnitsPerPixel());
    }
    int srcWidth = (int)(image.getWidth() / getZoom());
    int srcHeight = (int)(image.getHeight() / getZoom());
    painter.draw(image, x, y, getWidth(), getHeight(), 0, 0, srcWidth, srcHeight, false, true);
  }
}
