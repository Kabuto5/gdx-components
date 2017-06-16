package components;

import components.abstracts.GdxAbstractPager;
import components.interfaces.GdxComponent;

public class GdxPager extends GdxAbstractPager {
  public GdxPager(float x, float y, float width, float height, float span, Orientation orientation) {
    super(x, y, width, height, span, orientation);
  }
  
  public GdxPager(float x, float y, float width, float height, Orientation orientation) {
    super(x, y, width, height, 0, orientation);
  }

  @Override
  public void addItem(int position, GdxComponent item) {
    super.addItem(position, item);
  }

  @Override
  public void addItem(GdxComponent item) {
    super.addItem(item);
  }

  @Override
  public void removeItem(int position) {
    super.removeItem(position);
  }
}
