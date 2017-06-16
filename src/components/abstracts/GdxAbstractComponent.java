package components.abstracts;

import helpers.ComponentUtils;
import helpers.collections.DummyList;
import io.GdxInputListener;
import io.GdxPainter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import components.GdxMainFrame;
import components.aggregated.GdxListeners;
import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;
import effects.GdxVisualEffect;

public abstract class GdxAbstractComponent implements GdxComponent {
  private static final Plane CANVAS_PLANE = new Plane(new Vector3(0, 0, 1), 0);
  private static final DummyList<GdxVisualEffect> DUMMY_VISUAL_EFFECTS = new DummyList<GdxVisualEffect>();
  private static final Collection<GdxVisualEffect> DUMMY_UNMODIFIABLE_VISUAL_EFFECTS = Collections.unmodifiableCollection(DUMMY_VISUAL_EFFECTS);
  
  private GdxContainer container = null;
  private final Rectangle area;
  private boolean active = false;
  private boolean enabled = true;
  private boolean visible = true;
  private float interactiveAreaExtension = 0;
  private GdxListeners<GdxInputListener> inputListeners = new GdxListeners<GdxInputListener>();
  private List<GdxVisualEffect> visualEffects = DUMMY_VISUAL_EFFECTS;
  private Collection<GdxVisualEffect> unmodifiableVisualEffects = DUMMY_UNMODIFIABLE_VISUAL_EFFECTS;
  private BitSet pressingPointers = null;
  private int numberOfPressing = 0;
  private int numberOfDragging = 0;
  
  public GdxAbstractComponent(float x, float y, float width, float height) {
    area = new Rectangle(x, y, width, height);
  }

  @Override
  public void setContainer(GdxContainer container) {
    if (this.container != null && this.container.hasComponent(this))
      throw new IllegalStateException("Component is still contained in it's currently assigned container. Remove it before unassigning.");
    if (container != null && !container.hasComponent(this))
      throw new IllegalStateException("Component is not contained in the given container. Insert it into container before assigning.");
    this.container = container;
    if (getFrame() != null) {
      moved();
      ComponentUtils.makeAllDirty(this);
    }
  }
  
  @Override
  public GdxContainer getContainer() {
    return container;
  }

  @Override
  public float getX() {
    return area.x;
  }

  @Override
  public void setX(float x) {
    area.x = x;
    moved();
  }

  @Override
  public float getY() {
    return area.y;
  }

  @Override
  public void setY(float y) {
    area.y = y;
    moved();
  }
  
  @Override
  public Vector2 getLocation() {
    return new Vector2(area.x, area.y);
  }
  
  @Override
  public Vector2 getLocation(Vector2 out) {
    return out.set(area.x, area.y);
  }
  
  @Override
  public void setLocation(float x, float y) {
    area.x = x;
    area.y = y;
    moved();
  }
  
  @Override
  public void setLocation(Vector2 location) {
    area.x = location.x;
    area.y = location.y;
    moved();
  }
  
  @Override
  public float getWidth() {
    return area.width;
  }

  @Override
  public void setWidth(float width) {
    if (area.width != width) {
      area.width = width;
      resized();
    }
  }

  @Override
  public float getHeight() {
    return area.height;
  }

  @Override
  public void setHeight(float height) {
    if (area.height != height) {
      area.height = height;
      resized();
    }
  }
  
  @Override
  public Vector2 getSize() {
    return new Vector2(area.width, area.height);
  }
  
  @Override
  public Vector2 getSize(Vector2 out) {
    return out.set(area.width, area.height);
  }
  
  @Override
  public void setSize(float width, float height) {
    if (area.width != width || area.height != height) {
      area.width = width;
      area.height = height;
      resized();
    }
  }
  
  @Override
  public void setSize(Vector2 size) {
    setSize(size.x, size.y);
  }
  
  @Override
  public Rectangle getArea() {
    return new Rectangle(area.x, area.y, area.width, area.height);
  }
  
  @Override
  public Rectangle getArea(Rectangle out) {
    return out.set(area.x, area.y, area.width, area.height);
  }
  
  @Override
  public void setArea(float x, float y, float width, float height) {
    setLocation(x, y);
    setSize(width, height);
  }
  
  @Override
  public void setArea(Vector2 location, Vector2 size) {
    setArea(location.x, location.y, size.x, size.y);
  }
  
  @Override
  public void setArea(Rectangle area) {
    setArea(area.x, area.y, area.width, area.height);
  }
  
  @Override
  public GdxMainFrame getFrame() {
    if (container == null) return null;
    return container.getFrame();
  }

  @Override
  public float getFrameX() {
    if (container == null)
      throw new IllegalStateException("Component is not in frame.");
    return container.getFrameX() + area.x;
  }
  
  @Override
  public float getFrameY() {
    if (container == null)
      throw new IllegalStateException("Component is not in frame.");
    return container.getFrameY() + area.y;
  }
  
  @Override
  public Vector2 getFrameLocation() {
    if (container == null)
      throw new IllegalStateException("Component is not in frame.");
    Vector2 location = container.getFrameLocation();
    location.x += area.x;
    location.y += area.y;
    return location;
  }
  
  @Override
  public Vector2 getFrameLocation(Vector2 out) {
    if (container == null)
      throw new IllegalStateException("Component is not in frame.");
    container.getFrameLocation(out);
    out.x += area.x;
    out.y += area.y;
    return out;
  }
  
  @Override
  public Rectangle getFrameArea() {
    Vector2 location = getFrameLocation();
    return new Rectangle(location.x, location.y, area.width, area.height);
  }
  
  @Override
  public Rectangle getFrameArea(Rectangle out) {
    Vector2 location = getFrameLocation();
    return out.set(location.x, location.y, area.width, area.height);
  }
  
  /**
   * Is called whenever the size of the component itself has been changed.
   */
  protected void resized() {
    if (container != null) container.reportResize(this);
  }
  
  /**
   * Is called whenever the location of the component 
   * itself within it's container has been changed.
   * @see #reportMove(GdxContainer)
   */
  protected void moved() { }
  
  /**
   * Is called whenever a location of one of the component's 
   * containers in hierarchy has been changed.
   * @param container A specific container which has been moved
   * @see #moved()
   */
  public void reportMove(GdxContainer container) { }
  
  @Override
  public float getInteractiveAreaExtension() {
    return interactiveAreaExtension;
  }

  @Override
  public void setInteractiveAreaExtension(float size) {
    interactiveAreaExtension = size;
  }
  
  @Override
  public boolean isActive() {
    return active;
  }
  
  /**
   * Active component tracks pointers which are currently pressing on it or dragging it
   * and manages its pressed and dragged state. It consumes touch down and touch up events
   * and also consumes drag events as long as the given pointer is within their active area.
   * <p>
   * Pressing pointers are those currently positioned on component, while dragging pointers
   * started on component but may be curently positioned off it.
   */
  public void makeActive() {
    pressingPointers = new BitSet(10);
    active = true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public void addInputListener(Object tag, GdxInputListener inputListener) {
    inputListeners.add(tag, inputListener);
  }

  @Override
  public Collection<GdxInputListener> getInputListeners() {
    return inputListeners.getAll();
  }

  @Override
  public boolean removeInputListener(GdxInputListener inputListener) {
    return inputListeners.remove(inputListener);
  }

  @Override
  public GdxInputListener removeInputListener(Object tag) {
    return inputListeners.remove(tag);
  }
  
  @Override
  public void addVisualEffect(GdxVisualEffect visualEffect) {
    addVisualEffect(visualEffects.size(), visualEffect);
  }

  @Override
  public void addVisualEffect(int index, GdxVisualEffect visualEffect) {
    if (visualEffects == DUMMY_VISUAL_EFFECTS) {
      visualEffects = new ArrayList<GdxVisualEffect>(3);
      unmodifiableVisualEffects = Collections.unmodifiableCollection(visualEffects);
    }
    visualEffects.add(index, visualEffect);
    inputListeners.add(null, visualEffect);
  }
  
  @Override
  public GdxVisualEffect getVisualEffect(int index) {
    return visualEffects.get(index);
  }

  @Override
  public GdxVisualEffect getVisualEffect(Class<? extends GdxVisualEffect> visualEffectClass, int index) {
    for (GdxVisualEffect visualEffect : getVisualEffects()) {
      if (visualEffect.getClass().equals(visualEffectClass)) {
        if (index == 0) return visualEffect;
        index--;
      }
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<GdxVisualEffect> getVisualEffects() {
    return unmodifiableVisualEffects;
  }

  @Override
  public boolean removeVisualEffect(GdxVisualEffect visualEffect) {
    boolean removed = visualEffects.remove(visualEffect);
    inputListeners.remove(visualEffect);
    return removed;
  }

  @Override
  public GdxVisualEffect removeVisualEffect(int index) {
    GdxVisualEffect visualEffect = visualEffects.remove(index);
    inputListeners.remove(visualEffect);
    return visualEffect;
  }

  @Override
  public void removeVisualEffects(Class<? extends GdxVisualEffect> visualEffectClass, boolean dispose) {
    for (Iterator<GdxVisualEffect> it = visualEffects.iterator(); it.hasNext(); ) {
      GdxVisualEffect visualEffect = it.next();
      if (visualEffect.getClass().equals(visualEffectClass)) {
        it.remove();
        inputListeners.remove(visualEffect);
        if (dispose) visualEffect.dispose();
      }
    }
  }

  @Override
  public void clearVisualEffects(boolean dispose) {
    for (Iterator<GdxVisualEffect> it = visualEffects.iterator(); it.hasNext(); ) {
      GdxVisualEffect visualEffect = it.next();
      it.remove();
      inputListeners.remove(visualEffect);
      if (dispose) visualEffect.dispose();
    }
  }

  @Override
  public void clearVisualEffects() {
    clearVisualEffects(true);
  }

  @Override
  public void makeDirty() {
    GdxMainFrame frame = getFrame();
    if (frame != null) frame.reportDirty(this);
  }
  
  @Override
  public boolean insideActiveArea(float x, float y) {
    return x > - interactiveAreaExtension
        && y > - interactiveAreaExtension
        && x < area.width + interactiveAreaExtension
        && y < area.height + interactiveAreaExtension;
  }
  
  /**
   * Checks if a given picking ray (a straight line going from
   * the camera toward a point corresponding to a particular 
   * pixel on screen) intersects the component.
   * <p>
   * This is a default implementation, assuming two-dimensional (flat)
   * component with zero elevation (contained in a base plane),
   * using {@link GdxComponent#insideActiveArea(float, float) insideActiveArea}
   * method to determine whether or not the intersection happen.
   * <p>
   * The point of intersection with the base plane is always set,
   * even if component itself has not been intersected.
   * @param pickingRay Picking ray corresponding to a camera state
   * @param intersection Vector to be set to coordinates of intersection
   * @return Whether picking ray intersects the component
   */
  public boolean intersectRay(Ray pickingRay, Vector3 intersection) {
    Intersector.intersectRayPlane(pickingRay, CANVAS_PLANE, intersection);
    return insideActiveArea(intersection.x - getFrameX(), intersection.y - getFrameY());
  }
  
  public boolean isPressed() {
    return numberOfPressing > 0;
  }
  
  public int getNumberOfPressing() {
    return numberOfPressing;
  }

  public boolean isDragged() {
    return numberOfDragging > 0;
  }
  
  public int getNumberOfDragging() {
    return numberOfDragging;
  }
  
  @Override
  public void step(float delay) { }
  
  @Override
  public void renderTexture(int id, float width, float height, GdxPainter painter) { }
  
  @Override
  public abstract void paint(float x, float y, GdxPainter painter);
  
  @Override
  public void dispose() {
    clearVisualEffects();
  }
  
  @Override
  public boolean onTouchEvent(float x, float y, int pointer, int eventType) { 
    return false;
  }

  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    if (active) {
      pressingPointers.set(pointer, true);
      numberOfPressing++;
      numberOfDragging++;
      if (getNumberOfPressing() == 1) onPressed(x, y, pointer);
      if (getNumberOfDragging() == 1) onStartDrag(x, y, pointer);
      return true;
    } else {
      return onTouchEvent(x, y, pointer, GdxComponent.EVENT_TOUCH_DOWN);
    }
  }
  
  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    if (active) {
      if (pressingPointers.get(pointer)) {
        pressingPointers.set(pointer, false);
        numberOfPressing--;
        numberOfDragging--;
        if (getNumberOfPressing() == 0) onReleased(x, y, pointer);
        if (getNumberOfDragging() == 0) onStopDrag(x, y, pointer);
      } else {
        numberOfDragging--;
        if (getNumberOfDragging() == 0) onStopDrag(x, y, pointer);
      }
      return true;
    } else {
      return onTouchEvent(x, y, pointer, GdxComponent.EVENT_TOUCH_UP);
    }
  }
  
  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    if (active && isPressed()) {
      return true;
    } else {
      return onTouchEvent(differenceX, differenceY, pointer, GdxComponent.EVENT_TOUCH_DRAG);
    }
  }

  @Override
  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer) {
    if (active) {
      pressingPointers.set(pointer, true);
      numberOfPressing++;
      if (getNumberOfPressing() == 1) onPressed(x, y, pointer);
      return true;
    } else {
      return onTouchEvent(differenceX, differenceY, pointer, GdxComponent.EVENT_TOUCH_DRAGIN);
    }
  }

  @Override
  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer) {
    if (active) {
      pressingPointers.set(pointer, false);
      numberOfPressing--; 
      if (getNumberOfPressing() == 0) onReleased(x, y, pointer);
      return true;
    } else {
      return onTouchEvent(differenceX, differenceY, pointer, GdxComponent.EVENT_TOUCH_DRAGOUT);
    }
  }

  public boolean onMouseMove(float x, float y, float differenceX, float differenceY) {
    return onTouchEvent(differenceX, differenceY, 0, GdxComponent.EVENT_TOUCH_MOUSEMOVE);
  }

  public boolean onMouseOver(float x, float y, float differenceX, float differenceY) {
    return onTouchEvent(differenceX, differenceY, 0, GdxComponent.EVENT_TOUCH_MOUSEOVER);
  }

  public boolean onMouseOut(float x, float y, float differenceX, float differenceY) {
    return onTouchEvent(differenceX, differenceY, 0, GdxComponent.EVENT_TOUCH_MOUSEOUT);
  }

  @Override
  public boolean onTap(float x, float y, int tapCount, int pointer) {
    return onTouchEvent(x, y, pointer, GdxComponent.EVENT_TOUCH_TAP);
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    return onTouchEvent(velocityX, velocityY, pointer, GdxComponent.EVENT_TOUCH_FLING);
  }

  protected void onPressed(float x, float y, int pointer) { }
  
  protected void onReleased(float x, float y, int pointer) { }
  
  protected void onStartDrag(float x, float y, int pointer) { }
  
  protected void onStopDrag(float x, float y, int pointer) { }
}
