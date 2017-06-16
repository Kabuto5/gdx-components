package components.layouts;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

public class GdxBorderLayout implements GdxLayout {
  private enum Alignment { CENTER, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }
  private enum Stretch { NONE, FILL_CONTAINER, MATCH_CONTAINER }
  
  public static final Alignment ALIGN_CENTER = Alignment.CENTER;
  public static final Alignment ALIGN_NORTH = Alignment.NORTH;
  public static final Alignment ALIGN_NORTHEAST = Alignment.NORTHEAST;
  public static final Alignment ALIGN_EAST = Alignment.EAST;
  public static final Alignment ALIGN_SOUTHEAST = Alignment.SOUTHEAST;
  public static final Alignment ALIGN_SOUTH = Alignment.SOUTH;
  public static final Alignment ALIGN_SOUTHWEST = Alignment.SOUTHWEST;
  public static final Alignment ALIGN_WEST = Alignment.WEST;
  public static final Alignment ALIGN_NORTHWEST = Alignment.NORTHWEST;
  public static final Stretch STRETCH_NONE = Stretch.NONE;
  public static final Stretch STRETCH_FILL_CONTAINER = Stretch.FILL_CONTAINER;
  public static final Stretch STRETCH_MATCH_CONTAINER = Stretch.MATCH_CONTAINER;
  private static final int ALIGN_CENTER_INDEX = Alignment.CENTER.ordinal();
  private static final int ALIGN_NORTH_INDEX = Alignment.NORTH.ordinal();
  private static final int ALIGN_NORTHEAST_INDEX = Alignment.NORTHEAST.ordinal();
  private static final int ALIGN_EAST_INDEX = Alignment.EAST.ordinal();
  private static final int ALIGN_SOUTHEAST_INDEX = Alignment.SOUTHEAST.ordinal();
  private static final int ALIGN_SOUTH_INDEX = Alignment.SOUTH.ordinal();
  private static final int ALIGN_SOUTHWEST_INDEX = Alignment.SOUTHWEST.ordinal();
  private static final int ALIGN_WEST_INDEX = Alignment.WEST.ordinal();
  private static final int ALIGN_NORTHWEST_INDEX = Alignment.NORTHWEST.ordinal();
  private GdxComponent[] alignedComponents = new GdxComponent[9];
  private Stretch stretch;
  private float margin;
  
  public GdxBorderLayout(Stretch stretch, float margin) {
    clearAlignedComponents();
    setStretch(stretch);
    setMargin(margin);
  }
  
  public GdxBorderLayout(Stretch stretch) {
    this(stretch, 0);
  }
  
  public GdxBorderLayout(float margin) {
    this(STRETCH_NONE, margin);
  }
  
  public GdxBorderLayout() {
    this(STRETCH_NONE, 0);
  }

  public float getMargin() {
    return margin;
  }

  public void setMargin(float margin) {
    this.margin = margin;
  }
  
  public Stretch getStretch() {
    return stretch;
  }

  public void setStretch(Stretch stretch) {
    this.stretch = stretch;
  }

  public void setAlignedComponent(GdxComponent component, Alignment alignment) {
    for (int i = 0; i < alignedComponents.length; i++) {
      if (alignedComponents[i] == component)
        alignedComponents[i] = null;
    }
    try {
      alignedComponents[alignment.ordinal()] = component;
    }
    catch (ArrayIndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("Invalid component alignment");
    }
    GdxContainer container = component.getContainer();
    if (container != null) alignComponents(container);
  }
  
  public void clearAlignedComponents() {
    for (int i = 0; i < alignedComponents.length; i++) {
      alignedComponents[i] = null;
    }
  }

  @Override
  public void alignComponents(GdxContainer container) {
    float minX = 0;
    float minY = 0;
    float maxX = container.getWidth();
    float maxY = container.getHeight();
    if (stretch == STRETCH_FILL_CONTAINER) {
      minX = Math.max(minX, alignedComponents[ALIGN_NORTHWEST_INDEX].getWidth());
      minX = Math.max(minX, alignedComponents[ALIGN_WEST_INDEX].getWidth());
      minX = Math.max(minX, alignedComponents[ALIGN_SOUTHWEST_INDEX].getWidth());
      minY = Math.max(minY, alignedComponents[ALIGN_NORTHWEST_INDEX].getHeight());
      minY = Math.max(minY, alignedComponents[ALIGN_NORTH_INDEX].getHeight());
      minY = Math.max(minY, alignedComponents[ALIGN_NORTHEAST_INDEX].getHeight());
      maxX = Math.min(maxX, alignedComponents[ALIGN_NORTHEAST_INDEX].getX());
      maxX = Math.min(maxX, alignedComponents[ALIGN_EAST_INDEX].getX());
      maxX = Math.min(maxX, alignedComponents[ALIGN_SOUTHEAST_INDEX].getX());
      maxY = Math.min(maxY, alignedComponents[ALIGN_SOUTHWEST_INDEX].getY());
      maxY = Math.min(maxY, alignedComponents[ALIGN_SOUTH_INDEX].getY());
      maxY = Math.min(maxY, alignedComponents[ALIGN_SOUTHEAST_INDEX].getY());
    }
    int componentContainedFlags = 0;
    for (GdxComponent component : container.getComponents()) {
      if (alignedComponents[ALIGN_NORTH_INDEX] == component) {
        component.setLocation(
            (container.getWidth() - component.getWidth()) * 0.5f, margin);
        componentContainedFlags |= 1 << ALIGN_NORTH_INDEX;
      }
      else if (alignedComponents[ALIGN_NORTHEAST_INDEX] == component) {
        component.setLocation(
            container.getWidth() - component.getWidth() - margin, margin);
        componentContainedFlags |= 1 << ALIGN_NORTHEAST_INDEX;
      }
      else if (alignedComponents[ALIGN_EAST_INDEX] == component) {
        component.setLocation(
            container.getWidth() - component.getWidth() - margin,
            (container.getHeight() - component.getHeight()) * 0.5f);
        componentContainedFlags |= 1 << ALIGN_EAST_INDEX;
      }
      else if (alignedComponents[ALIGN_SOUTHEAST_INDEX] == component) {
        component.setLocation(
            container.getWidth() - component.getWidth() - margin,
            container.getHeight() - component.getHeight() - margin);
        componentContainedFlags |= 1 << ALIGN_SOUTHEAST_INDEX;
      }
      else if (alignedComponents[ALIGN_SOUTH_INDEX] == component) {
        component.setLocation(
            (container.getWidth() - component.getWidth()) * 0.5f,
            container.getHeight() - component.getHeight() - margin);
        componentContainedFlags |= 1 << ALIGN_SOUTH_INDEX;
      }
      else if (alignedComponents[ALIGN_SOUTHWEST_INDEX] == component) {
        component.setLocation(
            margin, container.getHeight() - component.getHeight() - margin);
        componentContainedFlags |= 1 << ALIGN_SOUTHWEST_INDEX;
      }
      else if (alignedComponents[ALIGN_WEST_INDEX] == component) {
        component.setLocation(
            margin, (container.getHeight() - component.getHeight()) * 0.5f);
        componentContainedFlags |= 1 << ALIGN_WEST_INDEX;
      }
      else if (alignedComponents[ALIGN_NORTHWEST_INDEX] == component) {
        component.setLocation(margin, margin);
        componentContainedFlags |= 1 << ALIGN_NORTHWEST_INDEX;
      }
      else if (alignedComponents[ALIGN_CENTER_INDEX] == component) {
        if (stretch == STRETCH_MATCH_CONTAINER || stretch == STRETCH_FILL_CONTAINER) {
          component.setArea(minX + margin, minY + margin, maxX - margin, maxY - margin);
        }
        else {
          component.setLocation(
              (container.getWidth() - component.getWidth()) * 0.5f,
              (container.getHeight() - component.getHeight()) * 0.5f);
        }
        componentContainedFlags |= 1 << ALIGN_CENTER_INDEX;
      }
    }
    /* Unlink all components which no longer are within container */
    for (int i = 0; i < alignedComponents.length; i++, componentContainedFlags >>= 1) {
      if ((componentContainedFlags & 1) == 0) {
        alignedComponents[i] = null;
      }
    }
  }
}
