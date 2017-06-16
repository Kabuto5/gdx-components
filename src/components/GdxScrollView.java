package components;

import components.abstracts.GdxAbstractScrollView;
import components.interfaces.GdxComponent;

public class GdxScrollView extends GdxAbstractScrollView {
  public GdxScrollView(float x, float y, float width, float height, GdxComponent content) {
    super(x, y, width, height, content);
  }
  
  public GdxScrollView(float x, float y, float width, float height) {
    super(x, y, width, height, null);
  }
  
  public float getScrollX() {
    return super.getScrollX();
  }
  
  public float getScrollY() {
    return super.getScrollY();
  }
  
  public void setScrollX(float scrollX) {
    super.setScrollX(scrollX);
  }
  
  public void setScrollY(float scrollY) {
    super.setScrollY(scrollY);
  }
  
  public void setScroll(float scrollX, float scrollY) {
    super.setScroll(scrollX, scrollY);
  }
  
  public GdxComponent getContent() {
    return super.getContent();
  }
  
  public void setContent(GdxComponent component) {
    super.setContent(component);
  }
}

