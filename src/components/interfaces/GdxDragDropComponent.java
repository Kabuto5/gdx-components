package components.interfaces;

import io.GdxPainter;
import components.GdxMainFrame;

public interface GdxDragDropComponent extends GdxComponent {
  /**
   * Tests if component is currently dragged and accepted to be dropped
   * onto another component. Other component must implement {@link GdxDragTarget} 
   * interface in order to possibly accept dragged components
   * @return Whether the component is currently accepted by another one.
   */
  public boolean isAccepted();
  
  /**
   * Returns an x-coordinate in relation to {@link GdxMainFrame frame}.
   * If component is not currently dragged, the return value is identical
   * to {@link #getFrameX() getFrameX}.
   * @return Current x-coordinate relative to component's frame.
   */
  public float getDragX();
  
  /**
   * Returns an y-coordinate in relation to {@link GdxMainFrame frame}.
   * If component is not currently dragged, the return value is identical
   * to {@link #getFrameY() getFrameY}.
   * @return Current y-coordinate relative to component's frame.
   */
  public float getDragY();
  
  /**
   * Paints a representation of the component while it's dragged.
   * @param x Coordinate relative to component's frame
   * @param y Coordinate relative to component's frame
   * @param painter Provides access to drawing operations
   */
  public void dragPaint(float x, float y, GdxPainter painter);
}
