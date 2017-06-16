package components;

import helpers.ComponentUtils;
import io.GdxAssets;
import io.GdxInputManager;
import io.GdxPainter;
import io.GdxPainter2D;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import components.aggregated.GdxListeners;
import components.interfaces.GdxComponent;
import components.interfaces.GdxDragDropComponent;
import components.interfaces.GdxUpdatable;
import components.layouts.GdxLayout;
import components.listeners.GdxLifecycleListener;

public class GdxMainFrame extends GdxPlainContainer implements ApplicationListener {
  public static final String TAG = GdxMainFrame.class.getSimpleName();
  
  private static final float MAX_DELTA_TIME = 0.125f;
  private final Class<? extends GdxPainter> painterClass;
  private GdxPainter painter;
  private GdxInputManager inputManager;
  private GdxAssets assets;
  private ArrayList<GdxUpdatable> currentDirties = new ArrayList<GdxUpdatable>();
  private ArrayList<GdxUpdatable> nextDirties = new ArrayList<GdxUpdatable>();
  private GdxListeners<GdxLifecycleListener> lifecycleListeners = new GdxListeners<GdxLifecycleListener>();
  private HashSet<GdxDragDropComponent> draggedComponents = new HashSet<GdxDragDropComponent>();
  private boolean skipDelay = false;

  public GdxMainFrame(float width, float height, Class<? extends GdxPainter> painterClass) {
    super(0, 0, width, height);
    this.painterClass = painterClass;
  }
  
  public GdxMainFrame(float width, float height) {
    this(width, height, GdxPainter2D.class);
  }

  /**
   * @return Currently assigned content or NULL
   */
  public GdxComponent getContent() {
    int componentCount = getComponentCount();
    if (componentCount == 0) {
      return null;
    } else if (componentCount == 1) {
      return getComponent(0);
    } else {
      throw new IllegalStateException("MainFrame contains more than one root component, which is deprecated. Please assign only a single component before using getContent()");
    }
  }

  /**
   * Assigns a new content. If different content is already assigned,
   * it is replaced but not disposed.
   * @param component Content to be assigned
   */
  public void setContent(GdxComponent component) {
    clearComponents();
    insertComponent(0, component);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public void addComponent(GdxComponent component) {
    super.addComponent(component);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public void addComponents(GdxComponent... components) {
    super.addComponents(components);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public void insertComponent(int index, GdxComponent component) {
    super.insertComponent(index, component);
    ComponentUtils.makeAllDirty(component);
  }

  /**
   * @deprecated This method may be removed in future. Assign only a single component 
   *             to the MainFrame and use {@link #getContent()} to access it.
   */
  @Override
  public GdxComponent getComponent(int index) {
    return super.getComponent(index);
  }

  /**
   * @deprecated This method may be removed in future. Assign only a single component 
   *             to the MainFrame and use {@link #getContent()} to access it.
   */
  @Override
  public int getComponentCount() {
    return super.getComponentCount();
  }

  /**
   * @deprecated This method may be removed in future. Assign only a single component 
   *             to the MainFrame and use {@link #getContent()} to access it.
   */
  @Override
  public int indexOf(GdxComponent component) {
    return super.indexOf(component);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public GdxComponent removeComponent(int index) {
    return super.removeComponent(index);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public void clearComponents(boolean dispose) {
    super.clearComponents(dispose);
  }

  /**
   * @deprecated This method may be removed in future. MainFrame should only contain 
   *             a single component assigned via {@link #setContent(GdxComponent)}
   */
  @Override
  public void clearComponents() {
    super.clearComponents();
  }

  /**
   * @deprecated This method may be removed in future. MainFrame is supposed to only contain
   *             a single component in future and therefore will have no use for layouts.
   */
  @Override
  public GdxLayout getLayout() {
    return super.getLayout();
  }

  /**
   * @deprecated This method may be removed in future. MainFrame is supposed to only contain
   *             a single component in future and therefore will have no use for layouts.
   */
  @Override
  public void setLayout(GdxLayout layout) {
    super.setLayout(layout);
  }

  public void addLifecycleListener(Object tag, GdxLifecycleListener listener) {
    lifecycleListeners.add(tag, listener);
  }

  public boolean removeLifecycleListener(GdxLifecycleListener listener) {
    return lifecycleListeners.remove(listener);
  }

  public GdxLifecycleListener removeLifecycleListener(Object tag) {
    return lifecycleListeners.remove(tag);
  }

  @Override
  public GdxMainFrame getFrame() {
    return this;
  }

  @Override
  public float getFrameX() {
    return getX();
  }

  @Override
  public float getFrameY() {
    return getY();
  }

  @Override
  public Vector2 getFrameLocation() {
    return getLocation();
  }

  @Override
  public Rectangle getFrameArea() {
    return getArea();
  }

  public void setBackgroundColor(Color backgroundColor) {
    painter.setClearColor(backgroundColor);
  }

  public Color getBackgroundColor() {
    return painter.getClearColor();
  }

  public GdxInputManager getInputManager() {
    return inputManager;
  }

  public GdxPainter getPainter() {
    return painter;
  }

  public GdxAssets getAssets() {
    return assets;
  }

  @Override
  protected void resized() {
    super.resized();
    if (painter != null) {
      painter.setSize(getWidth(), getHeight());
    }
  }

  @Override
  public void makeDirty() {
    reportDirty(this);
  }

  /**
   * Is called whenever an updatable needs to update it's state
   * depending on time passed between last and next frame, or
   * simply to be redrawn.
   * @param updatable Updatable which made a request
   */
  public void reportDirty(GdxUpdatable updatable) {
    if (!nextDirties.contains(updatable)) nextDirties.add(updatable);
  }

  /**
   * Is called whenever a draggable component began to be dragged.
   * Unless a container is a root container, it should report this 
   * event to it's parental container.
   * @param component Component which began to be dragged
   */
  public void dragStart(GdxDragDropComponent component) {
    draggedComponents.add(component);
  }

  /**
   * Is called whenever a draggable component stopped being dragged.
   * Unless a container is a root container, it should report this 
   * event to it's parental container.
   * @param component Component which stopped being dragged
   */
  public void dragStop(GdxDragDropComponent component) {
    draggedComponents.remove(component);
  }
  
  @Override
  public boolean insideActiveArea(float x, float y) {
    return true;
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    super.paint(x, y, painter);
    for (GdxDragDropComponent component : draggedComponents) {
      component.dragPaint(x + component.getDragX(), y + component.getDragY(), painter);
    }
  }

  private void updateDirties(float delay) {
    ArrayList<GdxUpdatable> undirtied = currentDirties;
    undirtied.clear();
    currentDirties = nextDirties;
    nextDirties = undirtied;
    for (GdxUpdatable updatable : currentDirties) {
      if (updatable.getFrame() == this) updatable.step(delay);
    }
  }

  @Override
  public void create() {
    Gdx.graphics.setContinuousRendering(false);
    try {
      painter = painterClass.getConstructor(float.class, float.class).newInstance(getWidth(), getHeight());
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
      throw new RuntimeException(exception);
    }
    inputManager = new GdxInputManager(this);
    assets = new GdxAssets(this);
    makeDirty();
    for (GdxLifecycleListener listener : lifecycleListeners) {
      listener.onCreate(this);
    }
  }

  @Override
  public void dispose() {
    for (GdxLifecycleListener listener : lifecycleListeners) {
      listener.onDispose(this);
    }
    super.dispose();
    inputManager.dispose();
    painter.dispose();
    assets.dispose();
  }

  @Override
  public void pause() {
    inputManager.clearInputs();
    for (GdxLifecycleListener listener : lifecycleListeners) {
      listener.onPause(this);
    }
  }

  @Override
  public void render() {
    if (skipDelay) {
      //Pretend that no time has passed since last render. This is to prevent sudden skips
      //in case no rendering happened for some time.
      updateDirties(0);
    } else if (Gdx.graphics.getRawDeltaTime() > MAX_DELTA_TIME) {
      updateDirties(MAX_DELTA_TIME);
    } else {
      updateDirties(Gdx.graphics.getRawDeltaTime());
    }
    if (nextDirties.isEmpty()) {
      //If no re-rendering was requested and therefore interface is inactive, ignore the next delay
      skipDelay = true;
    } else {
      Gdx.graphics.requestRendering();
      skipDelay = false;
    }
    painter.begin();
    painter.paintComponent(0, 0, this);
    painter.end();
  }

  @Override
  public void resize(int width, int height) {
    painter.setScreenSize(width, height);
    for (GdxLifecycleListener listener : lifecycleListeners) {
      listener.onResize(this, width, height);
    }
  }

  @Override
  public void resume() {
    for (GdxLifecycleListener listener : lifecycleListeners) {
      listener.onResume(this);
    }
  }
}
