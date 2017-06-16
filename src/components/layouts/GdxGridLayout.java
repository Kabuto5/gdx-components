package components.layouts;

import java.util.Collection;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

public class GdxGridLayout implements GdxLayout {
  private enum Direction { VERTICAL, HORIZONTAL };
  
  public static final Direction DIR_VERTICAL = Direction.VERTICAL;
  public static final Direction DIR_HORIZONTAL = Direction.HORIZONTAL;
  private float margin;
  private float spacing;
  private Direction direction;
  private boolean wrapContent;
  
  /**
   * 
   * @param margin Margin around the whole grid to be left empty
   * @param spacing Minimal spacing between individual components (some gaps may end up
   *                larger if components in grid vary in size)
   * @param direction Direction in which the grid is populated - vertical is by columns,
   *                  (starting from left) horizontal is by rows (starting from top).
   * @param wrapContent Whether container using this layout is to be resized to fit its content
   *               exactly
   */
  public GdxGridLayout(float margin, float spacing, Direction direction, boolean wrapContent) {
    this.margin = margin;
    this.spacing = spacing;
    this.direction = direction;
    this.wrapContent = wrapContent;
  }
  
  public GdxGridLayout(float margin, float spacing, Direction direction) {
    this(margin, spacing, direction, false);
  }

  public GdxGridLayout(float margin, float spacing) {
    this(margin, spacing, DIR_HORIZONTAL);
  }

  public GdxGridLayout(Direction direction) {
    this(0, 0, direction);
  }

  public GdxGridLayout() {
    this(DIR_HORIZONTAL);
  }
  
  public float getMargin() {
    return margin;
  }

  public void setMargin(float margin) {
    this.margin = margin;
  }

  public float getSpacing() {
    return spacing;
  }

  public void setSpacing(float spacing) {
    this.spacing = spacing;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public boolean isWrapContent() {
    return wrapContent;
  }

  public void setWrapContent(boolean wrapContent) {
    this.wrapContent = wrapContent;
  }

  @Override
  public void alignComponents(GdxContainer container) {
    Collection<GdxComponent> components = container.getComponents();
    float x = margin, y = margin;
    if (direction == DIR_HORIZONTAL) {
      float maxWidth = 0;
      float maxHeight = - spacing; // To prevent too wide to fit components
                                   // from spamming wider spacings
      for (GdxComponent component : components) {
        if (x + component.getWidth() + margin > container.getWidth()) {
          x = margin;
          y += maxHeight + spacing; // If component if first and the only in the current
                                    // row, this has no effect (see comment above)
          maxHeight = - spacing;
        }
        component.setLocation(x, y);
        if (component.getHeight() > maxHeight) {
          maxHeight = component.getHeight();
        }
        x += component.getWidth() + spacing;
        if (x > maxWidth) maxWidth = x;
      }
      maxWidth = maxWidth - spacing + margin;
      if (wrapContent) {
        container.setSize(maxWidth, y + maxHeight + margin);
      }
    }
    else {
      float maxHeight = 0;
      float maxWidth = - spacing; // To prevent too high to fit components
                                  // from spamming wider spacings
      for (GdxComponent component : components) {
        if (y + component.getHeight() + margin > container.getHeight()) {
          y = margin;
          x += maxWidth + spacing; // If component if first and the only in the current
                                   // column, this has no effect (see comment above)
          maxWidth = - spacing;
        }
        component.setLocation(x, y);
        if (component.getWidth() > maxWidth) {
          maxWidth = component.getWidth();
        }
        y += component.getHeight() + spacing;
        if (y > maxHeight) maxHeight = y;
      }
      maxHeight = maxHeight - spacing + margin;
      if (wrapContent) {
        container.setSize(x + maxWidth + margin, maxHeight);
      }
    }
  }
}
