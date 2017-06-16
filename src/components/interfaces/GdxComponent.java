package components.interfaces;

import io.GdxInputListener;
import io.GdxPainter;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

import effects.GdxVisualEffect;

public interface GdxComponent extends GdxUpdatable, GdxInputListener, Disposable {
  public static final int EVENT_TOUCH_DOWN = 0;
  public static final int EVENT_TOUCH_UP = 1;
  public static final int EVENT_TOUCH_DRAG = 2;
  public static final int EVENT_TOUCH_DRAGIN = 3;
  public static final int EVENT_TOUCH_DRAGOUT = 4;
  public static final int EVENT_TOUCH_TAP = 5;
  public static final int EVENT_TOUCH_FLING = 6;
  public static final int EVENT_TOUCH_MOUSEMOVE = 7;
  public static final int EVENT_TOUCH_MOUSEOVER = 8;
  public static final int EVENT_TOUCH_MOUSEOUT = 9;

  /**
   * Sets the link to the component container stored in the component
   * itself. Please note that this value have to always match the actual
   * container of the component and you <strong>should not attempt to
   * change</strong> this information from your code.
   * @param container Container in which the component is
   */
  public void setContainer(GdxContainer container);

  public GdxContainer getContainer();

  public float getX();

  public void setX(float x);

  public float getY();

  public void setY(float y);

  public Vector2 getLocation();

  public Vector2 getLocation(Vector2 out);

  public void setLocation(float x, float y);

  public void setLocation(Vector2 location);

  public float getWidth();

  public void setWidth(float width);

  public float getHeight();

  public void setHeight(float height);

  public Vector2 getSize();

  public Vector2 getSize(Vector2 out);

  public void setSize(float width, float height);

  public void setSize(Vector2 size);

  public Rectangle getArea();

  public Rectangle getArea(Rectangle out);

  public void setArea(float x, float y, float width, float height);

  public void setArea(Vector2 location, Vector2 size);

  public void setArea(Rectangle area);

  public float getFrameX();

  public float getFrameY();

  public Vector2 getFrameLocation();

  public Vector2 getFrameLocation(Vector2 out);

  public Rectangle getFrameArea();

  public Rectangle getFrameArea(Rectangle out);
  
  /**
   * Is called whenever a container containing this component changes
   * its position, resulting in component's position in frame changing.
   * @param container Container which's position changed
   */
  public void reportMove(GdxContainer container);

  public float getInteractiveAreaExtension();
  
  public void setInteractiveAreaExtension(float size);

  public boolean isActive();
  
  public boolean isEnabled();

  /**
   * Puts component into an enabled or disabled state.
   * Disabled component will not receive any events.
   * @param enabled If TRUE, component will be enabled,
   *        otherwise, component will be disabled.
   */
  public void setEnabled(boolean enabled);
  
  public boolean isVisible();
  
  public void setVisible(boolean visible);
  
  public void addInputListener(Object tag, GdxInputListener inputListener);
  
  public Collection<GdxInputListener> getInputListeners();
  
  public boolean removeInputListener(GdxInputListener inputListener);
  
  public GdxInputListener removeInputListener(Object tag);
  
  public void addVisualEffect(GdxVisualEffect visualEffect);
  
  public void addVisualEffect(int index, GdxVisualEffect visualEffect);
  
  public GdxVisualEffect getVisualEffect(int index);
  
  public GdxVisualEffect getVisualEffect(Class<? extends GdxVisualEffect> visualEffectClass, int index);
  
  public Collection<GdxVisualEffect> getVisualEffects();
  
  public boolean removeVisualEffect(GdxVisualEffect visualEffect);
  
  public GdxVisualEffect removeVisualEffect(int index);
  
  public void removeVisualEffects(Class<? extends GdxVisualEffect> visualEffectClass, boolean dispose);
  
  public void clearVisualEffects(boolean dispose);
  
  public void clearVisualEffects();
  
  /**
   * Checks if a given coordinates (relative to component's
   * origin) points inside component's active area (where it
   * responds to touches) in the base plane (surface with zero
   * elevation).
   * <p>
   * This is meant to allow modifications of component's
   * active area without actually moving or resizing it.
   * @param x x-coordinate in internal units
   * @param y y-coordinate in internal units
   * @return Whether coordinates points inside active area
   */
  public boolean insideActiveArea(float x, float y);

  /**
   * Checks if a given picking ray (a straight line going from
   * the camera toward a point corresponding to a particular 
   * pixel on screen) intersects the component. If so, point
   * of intersection is stored within the vector given as
   * a second parameter. Otherwise, the resulting state of
   * the vector is undefined.
   * @param pickingRay Picking ray corresponding to a camera state
   * @param intersection Vector to be set to coordinates of intersection
   * @return Whether picking ray intersects the component
   */
  public boolean intersectRay(Ray pickingRay, Vector3 intersection);

  /**
   * This method is called if component is asked to render a texture,
   * either at its own request or for some other component.
   * <p>
   * For this method to be called, component must call {@link 
   * GdxPainter#requestTextureRender(GdxComponent, int, float, float, Color)
   * requestTextureRender} method of the {@link GdxPainter}.
   * 
   * @param id Numeric identifier of a texture to be rendered.
   *           This might come in handy if a single component needs to
   *           perform multiple renderings.
   * @param width Width of a texture in internal units.
   * @param height Height of a texture in internal units.
   * @param painter Provides access to drawing operations.
   */
  public void renderTexture(int id, float width, float height, GdxPainter painter);

  /**
   * Any visual component has to paint itself here.
   * @param x component's coordinate relative to its frame's
   *          origin (same as {@link #getFrameX() getFrameX}
   *          method's result)
   * @param y component's coordinate relative to its frame's
   *          origin (same as {@link #getFrameY() getFrameY}
   *          method's result)
   * @param painter Provides access to drawing operations
   */
  public void paint(float x, float y, GdxPainter painter);
  
  /**
   * Override this if you're using any internal sources which needs to be disposed.
   */
  @Override
  public void dispose();
  
  /**
   * Allows to handle any events which originated from a touch screen or pointing device.
   * <p>
   * Parameters x and y store coordinates of the event occured, relative to component's origin, 
   * except for dragging events, where coordinates are relative to last reported location of 
   * the current pointer, and flinging events, where they store axial velocities instead.
   * @param x x-coordinate of the event or axial flinging velocity
   * @param y y-coordinate of the event or axial flinging velocity
   * @param pointer Numeric identifier asigned to a pointer causing event
   * @param eventType Type of event occured
   * @return TRUE if event was handled, FALSE otherwise
   * @deprecated Consider using more specific event handling method instead, like {@link #onTouchDown(float, float, int)}.
   *             Remember that by overriding a specific method, {@link #onTouchEvent(float, float, int, int)} will no longer
   *             be called for that specific event, unless you call super method or call it by yourself.
   */
  public boolean onTouchEvent(float x, float y, int pointer, int eventType);
  
  public class GdxEvent {
    public final Object type;
    public final Object data;
  
    public GdxEvent(Object type, Object data) {
      this.type = type;
      this.data = data;
    }
  
    public Object getType() {
      return type;
    }
  
    public Object getData() {
      return data;
    }
  }
}