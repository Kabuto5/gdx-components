package components;

import io.GdxPainter;

import components.abstracts.GdxAbstractComponent;

public class GdxSpacer extends GdxAbstractComponent {
  public GdxSpacer(float x, float y, float width, float height) {
    super(x, y, width, height);
  }

  public void paint(float x, float y, GdxPainter painter) { }
}
