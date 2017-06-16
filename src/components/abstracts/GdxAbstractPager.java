package components.abstracts;

import components.GdxPlainContainer;
import components.aggregated.GdxListeners;
import components.interfaces.GdxComponent;
import components.layouts.GdxLinearLayout;
import components.layouts.GdxLinearLayout.Alignment;
import components.listeners.GdxPageChangeListener;

public abstract class GdxAbstractPager extends GdxAbstractScrollView {
  public enum Orientation { VERTICAL, HORIZONTAL }
  
  public static final Orientation HORIZONTAL = Orientation.HORIZONTAL;
  public static final Orientation VERTICAL = Orientation.VERTICAL;

  private final GdxPlainContainer itemContainer;
  private final Orientation orientation;
  private GdxListeners<GdxPageChangeListener> pageChangeListeners = new GdxListeners<GdxPageChangeListener>();
  private int targetPosition = -1;
  
  public GdxAbstractPager(float x, float y, float width, float height, float span, Orientation orientation) {
    super(x, y, width, height, new GdxPlainContainer(0, 0, width, height));
    makeActive();
    itemContainer = (GdxPlainContainer)getContent();
    Alignment alignment;
    if (orientation == HORIZONTAL) {
      alignment = new Alignment(Alignment.HORIZONTAL, Alignment.LEFT, Alignment.CENTER_VERTICAL);
    } else {
      alignment = new Alignment(Alignment.VERTICAL, Alignment.CENTER_HORIZONTAL, Alignment.TOP);
    }
    itemContainer.setLayout(new GdxLinearLayout(0, span, alignment, true));
    this.orientation = orientation;
  }
  
  public void addPageChangeListener(Object tag, GdxPageChangeListener listener) {
    pageChangeListeners.add(tag, listener);
  }
  
  public boolean removePageChangeListener(GdxPageChangeListener listener) {
    return pageChangeListeners.remove(listener);
  }
  
  public GdxPageChangeListener removePageChangeListener(Object tag) {
    return pageChangeListeners.remove(tag);
  }
  
  public float getMaximumOvershoot() {
    return ((GdxLinearLayout)itemContainer.getLayout()).getMargin();
  }

  public void setMaximumOvershoot(float maximumOvershoot) {
    /* Set margin to the layout of the item container to allow overshoot */
    GdxLinearLayout itemContainerLayout = (GdxLinearLayout)itemContainer.getLayout();
    itemContainerLayout.setMargin(maximumOvershoot);
  }

  private void launchPageChangeEvent(int pageIndex) {
    for (GdxPageChangeListener listener : pageChangeListeners) {
      listener.onPageChange(this, pageIndex);
    }
  }
  
  private void launchPageSettleEvent(int pageIndex) {
    for (GdxPageChangeListener listener : pageChangeListeners) {
      listener.onPageSettle(this, pageIndex);
    }
  }
  
  @Override
  protected void resized() {
    super.resized();
    /* Adjust the non-scrollable dimension of the item container */
    if (orientation == HORIZONTAL) {
      itemContainer.setHeight(getHeight());
    } else {
      itemContainer.setHeight(getWidth());
    }
  }

  protected void addItem(int position, GdxComponent item) {
    itemContainer.insertComponent(position, item);
    if (itemContainer.getComponentCount() == 1) {
      selectItem(0, false);
    }
  }
  
  protected void addItem(GdxComponent item) {
    addItem(itemContainer.getComponentCount(), item);
  }
  
  protected void removeItem(int position) {
    int nearestPosition = getNearestPosition();
    if (position < nearestPosition) {
      selectItem(nearestPosition - 1, false);
    }
    itemContainer.removeComponent(position);
  }
  
  protected void selectItem(int position, boolean animated) {
    if (animated) {
      targetPosition = position;
      makeDirty();
    } else {
      targetPosition = -1;
      GdxComponent item = itemContainer.getComponent(position);
      if (orientation == HORIZONTAL) {
        setScrollX(item.getX() + (item.getWidth() - getWidth()) / 2);
      } else {
        setScrollY(item.getY() + (item.getHeight() - getHeight()) / 2);
      }
      launchPageChangeEvent(position);
      launchPageSettleEvent(position);
    }
  }

  @Override
  protected void updatePosition() {
    super.updatePosition();
    makeDirty();
  }

  protected int getNearestPosition() {
    int position = -1;
    float minDistance = Float.POSITIVE_INFINITY;
    if (orientation == HORIZONTAL) {
      float center = getWidth() / 2;
      for (GdxComponent item : itemContainer.getComponents()) {
        float distance = Math.abs(- getScrollX() + item.getX() + item.getWidth() / 2 - center);
        if (distance <= minDistance) minDistance = distance;
        else return position;
        position++;
      }
    } else {
      float center = getHeight() / 2;
      for (GdxComponent item : itemContainer.getComponents()) {
        float distance = Math.abs(- getScrollY() + item.getY() + item.getHeight() / 2 - center);
        if (distance <= minDistance) minDistance = distance;
        else return position;
        position++;
      }
    }
    return position;
  }
  
  @Override
  public void step(float delay) {
    super.step(delay);
    if (!isDragged() && getVelocityX() == 0 && getVelocityY() == 0 && !itemContainer.isEmpty()) {
      int nearestPosition = getNearestPosition();
      GdxComponent targetItem;
      if (nearestPosition == targetPosition) {
        targetPosition = -1;
        launchPageChangeEvent(nearestPosition);
      }
      if (targetPosition < 0) {
        targetItem = itemContainer.getComponent(nearestPosition);
      } else {
        targetItem = itemContainer.getComponent(targetPosition);
      }
      if (orientation == HORIZONTAL) {
        float adjustmentSpeed = 2 * getWidth();
        float movement = adjustmentSpeed * delay;
        float center = getWidth() / 2;
        float itemCenter = - getScrollX() + targetItem.getX() + targetItem.getWidth() / 2;
        if (itemCenter > center) {
          if (itemCenter - movement <= center) {
            movement = itemCenter - center;
          }
          setScrollX(getScrollX() + movement);
          makeDirty();
        } else if (itemCenter < center) {
          if (itemCenter + movement >= center) {
            movement = center - itemCenter;
          }
          setScrollX(getScrollX() - movement);
          makeDirty();
        } else {
          launchPageSettleEvent(nearestPosition);
        }
      } else {
        float adjustmentSpeed = 2 * getHeight();
        float movement = adjustmentSpeed * delay;
        float center = getHeight() / 2;
        float itemCenter = - getScrollY() + targetItem.getY() + targetItem.getHeight() / 2;
        if (itemCenter > center) {
          if (itemCenter - movement <= center) {
            movement = itemCenter - center;
          }
          setScrollY(getScrollY() + movement);
          makeDirty();
        } else if (itemCenter < center) {
          if (itemCenter + movement >= center) {
            movement = center - itemCenter;
          }
          setScrollY(getScrollY() - movement);
          makeDirty();
        } else {
          launchPageSettleEvent(nearestPosition);
        }
      }
    }
  }

  @Override
  protected void onStartDrag(float x, float y, int pointer) {
    super.onStartDrag(x, y, pointer);
  }

  @Override
  protected void onStopDrag(float x, float y, int pointer) {
    super.onStopDrag(x, y, pointer);
    targetPosition = -1;
    launchPageChangeEvent(getNearestPosition());
    makeDirty();
  }
}
