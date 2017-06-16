package components.layouts;

import java.util.Collection;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

public class GdxLinearLayout implements GdxLayout {
  private enum Orientation { VERTICAL, HORIZONTAL }
  private enum VerticalAlignment { CENTER, TOP, BOTTOM }
  private enum HorizontalAlignment { CENTER, LEFT, RIGHT }
  private enum SpanStretching { NONE, EXTEND, COMPRESS }
  
  @Deprecated
  public static final int ALIGN_VERTICAL = 0;  
  @Deprecated
  public static final int ALIGN_HORIZONTAL = 1;  
  @Deprecated
  public static final int ALIGN_CENTER = 0;  
  @Deprecated
  public static final int ALIGN_LEFT = 2;  
  @Deprecated
  public static final int ALIGN_RIGHT = 4;  
  @Deprecated
  public static final int ALIGN_TOP = 8;  
  @Deprecated
  public static final int ALIGN_BOTTOM = 16;
  @Deprecated
  public static final int ALIGN_EXTEND_SPAN = 32;
  @Deprecated
  public static final int ALIGN_COMPRESS_SPAN = 64;
  private float margin;
  private float span;
  private Alignment alignment;
  private boolean wrapContent;
  
  public GdxLinearLayout(float margin, float span, Alignment alignment, boolean wrapContent) {
    this.margin = margin;
    this.span = span;
    this.alignment = alignment;
    this.wrapContent = wrapContent;
  }
  
  public GdxLinearLayout(float margin, float span, Alignment alignment) {
    this(margin, span, alignment, false);
  }

  @Deprecated
  public GdxLinearLayout(float margin, float span, int alignment) {
    this(margin, span, new Alignment(alignment));
  }

  public GdxLinearLayout(float margin, float span) {
    this(margin, span, 0);
  }

  public GdxLinearLayout(Alignment alignment) {
    this(0, 0, alignment);
  }

  @Deprecated
  public GdxLinearLayout(int alignment) {
    this(0, 0, new Alignment(alignment));
  }

  public GdxLinearLayout() {
    this(0);
  }
  
  public float getMargin() {
    return margin;
  }

  public void setMargin(float margin) {
    this.margin = margin;
  }

  public float getSpan() {
    return span;
  }

  public void setSpan(float span) {
    this.span = span;
  }

  public Alignment getAlignment() {
    return alignment;
  }

  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }

  public boolean isWrapContent() {
    return wrapContent;
  }

  public void setWrapContent(boolean wrapContent) {
    this.wrapContent = wrapContent;
  }

  private void setComponentX(GdxContainer container, GdxComponent component) {
    if (alignment.horizontalAlignment == Alignment.LEFT) {
      component.setX(margin);
    }
    else if (alignment.horizontalAlignment == Alignment.RIGHT) {
      component.setX(container.getWidth() - component.getWidth() - margin);
    }
    else {
      component.setX((container.getWidth() - component.getWidth()) * 0.5f);
    }
  }

  private void setComponentY(GdxContainer container, GdxComponent component) {
    if (alignment.verticalAlignment == Alignment.TOP) {
      component.setY(margin);
    }
    else if (alignment.verticalAlignment == Alignment.BOTTOM) {
      component.setY(container.getHeight() - component.getHeight() - margin);
    }
    else {
      component.setY((container.getHeight() - component.getHeight()) * 0.5f);
    }
  }
  
  public void alignComponents(GdxContainer container) {
    Collection<GdxComponent> components = container.getComponents();
    if (alignment.orientation == Alignment.HORIZONTAL) {
      float x, spanX;
      if (wrapContent) {
        x = margin;
        spanX = span;
      } else {
        if (alignment.spanStretching != SpanStretching.NONE && components.size() > 1) {
          float widthSum = 0;
          for (GdxComponent component : components) {
            widthSum += component.getWidth();
          }
          spanX = (container.getWidth() - 2 * margin - widthSum) / (components.size() - 1);
          if (alignment.spanStretching == SpanStretching.EXTEND && span > spanX) {
            spanX = span;
          } else if (alignment.spanStretching == SpanStretching.COMPRESS && span < spanX) {
            spanX = span;
          }
        } else {
          spanX = span;
        }
        if (alignment.horizontalAlignment == Alignment.LEFT) {
          x = margin;
        } else if (alignment.horizontalAlignment == Alignment.RIGHT) {
          x = 0;
          for (GdxComponent component : components) {
            x += component.getWidth() + spanX;
          }
          x = container.getWidth() - x + spanX - margin;
        } else {
          x = 0;
          for (GdxComponent component : components) {
            x += component.getWidth() + spanX;
          }
          x = (container.getWidth() - x + spanX) * 0.5f;
        }
      }
      for (GdxComponent component : components) {
        component.setX(x);
        setComponentY(container, component);
        x += component.getWidth() + spanX;
      }
      if (wrapContent) {
        container.setWidth(x - spanX + margin);
      }
    }
    else {
      float y, spanY;
      if (wrapContent) {
        y = margin;
        spanY = span;
      } else {
        if (alignment.spanStretching != SpanStretching.NONE && components.size() > 1) {
          float heightSum = 0;
          for (GdxComponent component : components) {
            heightSum += component.getHeight();
          }
          spanY = (container.getHeight() - 2 * margin - heightSum) / (components.size() - 1);
          if (alignment.spanStretching == SpanStretching.EXTEND && span > spanY) {
            spanY = span;
          } else if (alignment.spanStretching == SpanStretching.COMPRESS && span < spanY) {
            spanY = span;
          }
        } else {
          spanY = span;
        }
        if (alignment.verticalAlignment == Alignment.TOP) {
          y = margin;
        }
        else if (alignment.verticalAlignment == Alignment.BOTTOM) {
          y = 0;
          for (GdxComponent component : components) {
            y += component.getHeight() + spanY;
          }
          y = container.getHeight() - y + spanY - margin;
        }
        else {
          y = 0;
          for (GdxComponent component : components) {
            y += component.getHeight() + spanY;
          }
          y = (container.getHeight() - y + spanY) * 0.5f;
        }
      }
      for (GdxComponent component : components) {
        component.setY(y);
        setComponentX(container, component);
        y += component.getHeight() + spanY;
      }
      if (wrapContent) {
        container.setHeight(y - spanY + margin);
      }
    }
  }
  
  public static class Alignment {
    public static final Orientation HORIZONTAL = Orientation.HORIZONTAL;
    public static final Orientation VERTICAL = Orientation.VERTICAL;
    public static final HorizontalAlignment CENTER_HORIZONTAL = HorizontalAlignment.CENTER;
    public static final HorizontalAlignment LEFT = HorizontalAlignment.LEFT;
    public static final HorizontalAlignment RIGHT = HorizontalAlignment.RIGHT;
    public static final VerticalAlignment CENTER_VERTICAL = VerticalAlignment.CENTER;
    public static final VerticalAlignment TOP = VerticalAlignment.TOP;
    public static final VerticalAlignment BOTTOM = VerticalAlignment.BOTTOM;
    public static final SpanStretching FIXED_SPAN = SpanStretching.NONE;
    /**
     * Space between components will be extended in order to lay components 
     * across the container evenly, but it never goes under it's preset size.
     */
    public static final SpanStretching EXTEND_SPAN = SpanStretching.EXTEND;
    /**
     * If necessary, space between components will be compressed in order to fit
     * all components into container, but components are never made to overlap.
     */
    public static final SpanStretching COMPRESS_SPAN = SpanStretching.COMPRESS;
  
    public final Orientation orientation;
    public final HorizontalAlignment horizontalAlignment;
    public final VerticalAlignment verticalAlignment;
    public final SpanStretching spanStretching;
    
    public Alignment(Orientation orientation, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, SpanStretching spanStretching) {
      this.orientation = orientation;
      this.horizontalAlignment = horizontalAlignment;
      this.verticalAlignment = verticalAlignment;
      this.spanStretching = spanStretching;
    }

    public Alignment(Orientation orientation, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
      this(orientation, horizontalAlignment, verticalAlignment, SpanStretching.NONE);
    }

    /**
     * Only for compatibility purposes.
     * @param alignment Flag field containing all alignment parameters
     */
    @Deprecated
    public Alignment(int alignment) {
      if ((alignment & ALIGN_HORIZONTAL) > 0) {
        orientation = HORIZONTAL;
      } else {
        orientation = VERTICAL;
      }
      if ((alignment & ALIGN_LEFT) > 0) {
        horizontalAlignment = LEFT;
      } else if ((alignment & ALIGN_RIGHT) > 0) {
        horizontalAlignment = RIGHT;
      } else {
        horizontalAlignment = CENTER_HORIZONTAL;
      }
      if ((alignment & ALIGN_TOP) > 0) {
        verticalAlignment = TOP;
      } else if ((alignment & ALIGN_BOTTOM) > 0) {
        verticalAlignment = BOTTOM;
      } else {
        verticalAlignment = CENTER_VERTICAL;
      }
      if ((alignment & ALIGN_EXTEND_SPAN) > 0) {
        spanStretching = EXTEND_SPAN;
      } else if ((alignment & ALIGN_COMPRESS_SPAN) > 0) {
        spanStretching = COMPRESS_SPAN;
      } else {
        spanStretching = FIXED_SPAN;
      }
    }
    /**
     * Only for compatibility purposes.
     * @return alignment Flag field containing all alignment parameters
     */
    @Deprecated
    public int getAlignment() {
      int alignment = 0;
      switch (orientation) {
        case VERTICAL: alignment += ALIGN_VERTICAL; break;
        case HORIZONTAL: alignment += ALIGN_HORIZONTAL; break;
      }
      switch (horizontalAlignment) {
        case CENTER: alignment += ALIGN_CENTER; break;
        case LEFT: alignment += ALIGN_LEFT; break;
        case RIGHT: alignment += ALIGN_RIGHT; break;
      }
      switch (verticalAlignment) {
        case CENTER: alignment += ALIGN_CENTER; break;
        case TOP: alignment += ALIGN_TOP; break;
        case BOTTOM: alignment += ALIGN_BOTTOM; break;
      }
      switch (spanStretching) {
        case EXTEND: alignment += ALIGN_EXTEND_SPAN; break;
        case COMPRESS: alignment += ALIGN_COMPRESS_SPAN; break;
        case NONE: /* No flag to set */ break;
      }
      return alignment;
    }
  }
}
