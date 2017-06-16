package components.abstracts;

import io.GdxPainter;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

/**
 * Skeletal implementation of the {@link GdxContainer} interface, providing basic functionality
 * which is likely to be shared by all standard container types.
 * 
 */
public abstract class GdxAbstractContainer extends GdxAbstractComponent implements GdxContainer {
  public GdxAbstractContainer(float x, float y, float width, float height) {
    super(x, y, width, height);
  }
  
  @Override
  protected void moved() {
    super.moved();
    for (GdxComponent component : getComponents()) {
      component.reportMove(this);
    }
  }
  
  @Override
  public void reportMove(GdxContainer container) {
    super.reportMove(container);
    for (GdxComponent component : getComponents()) {
      component.reportMove(container);
    }
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    for (GdxComponent component : getComponents()) {
      painter.paintComponent(x + component.getX(), y + component.getY(), component);
    }
  }

  @Override
  public boolean onDragReceived(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  @Override
  public void onDragCapturingStopped(float x, float y, int pointer) { }
}
