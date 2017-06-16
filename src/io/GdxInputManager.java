package io;

import helpers.collections.SingleElementIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

import components.GdxMainFrame;
import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;
import components.interfaces.GdxSingletouchComponent;

public class GdxInputManager implements InputProcessor, Disposable {
  public static final String TAG = GdxInputManager.class.getSimpleName();
  
  private static final long DEFAULT_MULTITAP_MAX_DELAY = 1000000000L; //Nanoseconds
  private static final long DEFAULT_TAP_MAX_DURATION = 333333333L; //Nanoseconds
  private static final float DEFAULT_TAP_MAX_DRAG_DISTANCE = 0.5f; //Centimeters, measured per axis
  private final GdxMainFrame mainFrame;
  private final GdxPainter painter;
  private final HashMap<Integer, PointerRecord> pointers = new HashMap<Integer, PointerRecord>();
  private final HashMap<GdxComponent, TapCounter> tapCounters = new HashMap<GdxComponent, TapCounter>();
  private Vector2 cursorPosition;
  private GdxComponent currentControl, mouseOver;
  private boolean multitouchEnabled = true;
  private boolean crossComponentMultitouchEnabled = false;
  private long multitapMaxDelay = DEFAULT_MULTITAP_MAX_DELAY;
  private long tapMaxDuration = DEFAULT_TAP_MAX_DURATION;
  private float tapMaxDragDistanceX, tapMaxDragDistanceY;
  
  public GdxInputManager(GdxMainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.painter = mainFrame.getPainter();
    InputProcessor inputProcessor = Gdx.input.getInputProcessor();
    if (inputProcessor == null || inputProcessor instanceof InputMultiplexer) {
      inputProcessor = new InputMultiplexer();
      Gdx.input.setInputProcessor(inputProcessor);
    }
    ((InputMultiplexer)inputProcessor).addProcessor(this);
    setTapMaxDragDistanceCentimeters(DEFAULT_TAP_MAX_DRAG_DISTANCE);
  }

  public boolean isMultitouchEnabled() {
    return multitouchEnabled;
  }
  
  public void enableMultitouch(boolean multitouchEnabled) {
    this.multitouchEnabled = multitouchEnabled;
  }
  
  public boolean isCrossComponentMultitouchEnabled() {
    return crossComponentMultitouchEnabled;
  }
  
  public void enableCrossComponentMultitouch(boolean crossComponentMultitouchEnabled) {
    this.crossComponentMultitouchEnabled = crossComponentMultitouchEnabled;
  }
  
  public float getMultitapMaxDelay() {
    return multitapMaxDelay / 1000000000f; //Convert from nanoseconds to seconds
  }

  public void setMultitapMaxDelay(float seconds) {
    multitapMaxDelay = (long)(seconds * 1000000000L); //Convert from seconds to nanoseconds
  }

  public float getTapMaxDuration() {
    return tapMaxDuration / 1000000000f; //Convert from nanoseconds to seconds
  }
  
  public void setTapMaxDuration(float seconds) {
    tapMaxDuration = (long)(seconds * 1000000000L); //Convert from seconds to nanoseconds
  }
  
  public float getTapMaxDragDistanceInches() {
    return tapMaxDragDistanceX / Gdx.graphics.getPpiX();
  }
  
  public void setTapMaxDragDistanceInches(float inches) {
    tapMaxDragDistanceX = inches * Gdx.graphics.getPpiX();
    tapMaxDragDistanceY = inches * Gdx.graphics.getPpiY();
  }
  
  public float getTapMaxDragDistanceCentimeters() {
    return tapMaxDragDistanceX / Gdx.graphics.getPpcX();
  }
  
  public void setTapMaxDragDistanceCentimeters(float centimeters) {
    tapMaxDragDistanceX = centimeters * Gdx.graphics.getPpcX();
    tapMaxDragDistanceY = centimeters * Gdx.graphics.getPpcY();
  }
  
  private boolean noRegisteredTouch() {
    return pointers.size() == 0;
  }
  
  private boolean noRegisteredTouchOnComponent(GdxComponent component) {
    for (PointerRecord record : pointers.values()) {
      if (record.component == component) return false;
    }
    return true;
  }
  
  private int numberOfRegisteredTouchesOnComponent(GdxComponent component) {
    int counter = 0;
    for (PointerRecord record : pointers.values()) {
      if (record.component == component) counter++;
    }
    return counter;
  }
  
  private int countTapOnComponent(GdxComponent component) {
    int numberOfTouches = numberOfRegisteredTouchesOnComponent(component);
    TapCounter tapCounter = tapCounters.get(component);
    if (tapCounter == null) {
      tapCounter = new TapCounter(numberOfTouches);
      tapCounters.put(component, tapCounter);
      return 1;
    } else {
      return tapCounter.countTap(numberOfTouches);
    }
  }
  
  private void clearExpiredTapCounters() {
    for (Iterator<TapCounter> it = tapCounters.values().iterator(); it.hasNext(); ) {
      if (it.next().getTapCount() == 0) it.remove();
    }
  }
  
  public void dispose() {
    InputProcessor inputProcessor = Gdx.input.getInputProcessor();
    if (inputProcessor != null && inputProcessor instanceof InputMultiplexer) {
      ((InputMultiplexer)inputProcessor).removeProcessor(this);
    }
  }
  
  protected List<IntersectedComponent> getIntersected(Iterator<GdxComponent> candidateIterator, Ray pickingRay,
      List<IntersectedComponent> intersected) {
    while (candidateIterator.hasNext()) {
      GdxComponent component = candidateIterator.next();
      if (component.isEnabled()) {
        Vector3 intersection = new Vector3();
        if (component.intersectRay(pickingRay, intersection)) {
          float distance = intersection.dst(pickingRay.origin);
          intersected.add(new IntersectedComponent(component, intersection, distance));
        }
      }
    }
    Collections.sort(intersected);
    return intersected;
  }
  
  protected boolean processTouchDown(Iterator<GdxComponent> candidateIterator, int screenX, int screenY, int pointer) {
    Ray pickingRay = painter.getPickingRay(screenX, screenY);
    List<IntersectedComponent> intersected = getIntersected(candidateIterator, pickingRay, new ArrayList<IntersectedComponent>());
    for (IntersectedComponent candidate : intersected) {
      GdxComponent component = candidate.component;
      Vector3 intersection = candidate.intersection;
      //If the component is a container, event is propagated to its children
      if (component instanceof GdxContainer
          && processTouchDown(
              ((GdxContainer)component).interactionCandidatesIterator(intersection.x, intersection.y), 
              screenX, screenY, pointer)) {
        return true;
      }
      //If event wasn't processed by any child or component isn't a container,
      //event is propagated to the component itself.
      if ((crossComponentMultitouchEnabled || currentControl == null || currentControl == component)
          && (!(component instanceof GdxSingletouchComponent) || noRegisteredTouchOnComponent(component))
          && sendTouchDownEvent(component, intersection.x - component.getFrameX(), intersection.y - component.getFrameY(), pointer)) {
        pointers.put(pointer, new PointerRecord(intersection, component));
        currentControl = component;
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    clearExpiredTapCounters();
    if (multitouchEnabled || noRegisteredTouch()) {
      return processTouchDown(SingleElementIterator.create((GdxComponent)mainFrame), screenX, screenY, pointer);
    }
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    PointerRecord record = pointers.remove(pointer);
    if (record != null && record.component.isEnabled()) {
      GdxComponent component = record.component;
      Vector3 intersection = new Vector3();
      Intersector.intersectRayPlane(painter.getPickingRay(screenX, screenY), record.plane, intersection);
      float componentX = intersection.x - component.getFrameX();
      float componentY = intersection.y - component.getFrameY();
      boolean handled;
      //Touch up event is never propagated to component's parent
      //because it would otherwise receive touch up without
      //preceding touch down event.
      boolean touchUpHandled = sendTouchUpEvent(record, component, componentX, componentY, pointer);
      handled = touchUpHandled;
      if (record.startTime - System.nanoTime() + tapMaxDuration >= 0) {
        float ppcu = painter.getPpcu();
        if (Math.abs(record.startX - record.x) * ppcu <= tapMaxDragDistanceX
            && Math.abs(record.startY - record.y) * ppcu <= tapMaxDragDistanceY) {
          int tapCount = countTapOnComponent(component);
          boolean tapHandled;
          if (touchUpHandled)
            tapHandled = sendTapEvent(component, componentX, componentY, tapCount, pointer);
          else
            tapHandled = propagateTapEvent(component, componentX, componentY, tapCount, pointer);
          if (tapHandled) tapCounters.remove(component);
          handled = tapHandled || handled;
        }
      }
      float diffX = record.x - record.previousX;
      float diffY = record.y - record.previousY;
      if (diffX != 0 || diffY != 0) {
        float timeDiff = (System.nanoTime() - record.previousTime) * 0.000000001f;
        float velocityX = diffX / timeDiff;
        float velocityY = diffY / timeDiff;
        handled = propagateFlingEvent(component, componentX, componentY, velocityX, velocityY, pointer) || handled;
      }
      if (noRegisteredTouch()) currentControl = null;
      return handled;
    }
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    PointerRecord record = pointers.get(pointer);
    if (record != null) {
      GdxComponent component = record.component;
      GdxComponent receiver = record.dragReceiver;
      Ray pickingRay = painter.getPickingRay(screenX, screenY);
      Vector3 intersection = new Vector3();
      Intersector.intersectRayPlane(pickingRay, record.plane, intersection);
      float dragX = intersection.x - record.x;
      float dragY = intersection.y - record.y;
      record.setX(intersection.x);
      record.setY(intersection.y);
      record.setTime(System.nanoTime());
      float receiverX = intersection.x - receiver.getFrameX();
      float receiverY = intersection.y - receiver.getFrameY();
      boolean inside = receiver.intersectRay(pickingRay, intersection);
      boolean handled = false;
      if (component == receiver) {
        float componentX = intersection.x - component.getFrameX();
        float componentY = intersection.y - component.getFrameY();
        //Sends occured drag in/drag out event to dragged component itself only.
        if (inside && record.outside && receiver.isEnabled()) {
          handled = sendDragInEvent(receiver, componentX, componentY, dragX, dragY, pointer) || handled;
          record.outside = false;
        }
        else if (!inside && !record.outside && receiver.isEnabled()) {
          handled = sendDragOutEvent(receiver, componentX, componentY, dragX, dragY, pointer) || handled;
          record.outside = true;
        }
      }
      //Unprocessed drag event is propagated to component's parent.
      handled = propagateDragEvent(record, receiverX, receiverY, dragX, dragY, pointer) || handled;
      return handled;
    }
    return false;
  }
  
  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }
  
  protected boolean processMouseMoved(Iterator<GdxComponent> candidateIterator, int screenX, int screenY) {
    Ray pickingRay = painter.getPickingRay(screenX, screenY);
    List<IntersectedComponent> intersected = getIntersected(candidateIterator, pickingRay, new ArrayList<IntersectedComponent>());
    if (!intersected.isEmpty()) {
      IntersectedComponent candidate = intersected.get(0);
      GdxComponent component = candidate.component;
      Vector3 intersection = candidate.intersection;
      if (cursorPosition == null) cursorPosition = new Vector2(intersection.x, intersection.y);
      //If the component is a container, event is propagated to its children
      if (component instanceof GdxContainer
          && processMouseMoved(
              ((GdxContainer)component).interactionCandidatesIterator(intersection.x, intersection.y), 
              screenX, screenY)) {
        return true;
      }
      //If event wasn't processed by any child or component isn't a container,
      //event is propagated to the component itself.
      float moveX = intersection.x - cursorPosition.x;
      float moveY = intersection.y - cursorPosition.y;
      cursorPosition.x = intersection.x;
      cursorPosition.y = intersection.y;
      float componentX = intersection.x - component.getFrameX();
      float componentY = intersection.y - component.getFrameY();
      if (component.equals(mouseOver)) {
        sendMouseMoveEvent(component, componentX, componentY, moveX, moveY);
      } else {
        if (mouseOver != null) {
          sendMouseOutEvent(mouseOver, componentX, componentY, moveX, moveY);
        }
        sendMouseOverEvent(component, componentX, componentY, moveX, moveY);
        mouseOver = component;
      }
      return true;
    }
    return false;
  }
  
  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    if (processMouseMoved(SingleElementIterator.create((GdxComponent)mainFrame), screenX, screenY)) {
      return true;
    }
    mouseOver = null;
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
  
  protected boolean sendTouchDownEvent(GdxComponent component, float x, float y, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onTouchDown(x, y, pointer)) return true;
    }
    return component.onTouchDown(x, y, pointer);
  }
  
  protected boolean sendTouchUpEvent(PointerRecord record, GdxComponent component, float x, float y, int pointer) {
    if (record.dragReceiver != component) {
      ((GdxContainer)record.dragReceiver).onDragCapturingStopped(x, y, pointer);
    }
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onTouchUp(x, y, pointer)) return true;
    }
    return component.onTouchUp(x, y, pointer);
  }

  protected boolean sendDragEvent(GdxComponent component, float x, float y, float differenceX, float differenceY, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onDrag(x, y, differenceX, differenceY, pointer)) return true;
    }
    return component.onDrag(x, y, differenceX, differenceY, pointer);
  }

  protected boolean sendDragInEvent(GdxComponent component, float x, float y, float differenceX, float differenceY, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onDragIn(x, y, differenceX, differenceY, pointer)) return true;
    }
    return component.onDragIn(x, y, differenceX, differenceY, pointer);
  }

  protected boolean sendDragOutEvent(GdxComponent component, float x, float y, float differenceX, float differenceY, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onDragOut(x, y, differenceX, differenceY, pointer)) return true;
    }
    return component.onDragOut(x, y, differenceX, differenceY, pointer);
  }
  
  protected boolean sendMouseMoveEvent(GdxComponent component, float x, float y, float differenceX, float differenceY) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onMouseMove(x, y, differenceX, differenceY)) return true;
    }
    return component.onMouseMove(x, y, differenceX, differenceY);
  }
  
  protected boolean sendMouseOverEvent(GdxComponent component, float x, float y, float differenceX, float differenceY) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onMouseOver(x, y, differenceX, differenceY)) return true;
    }
    return component.onMouseOver(x, y, differenceX, differenceY);
  }

  protected boolean sendMouseOutEvent(GdxComponent component, float x, float y, float differenceX, float differenceY) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onMouseOut(x, y, differenceX, differenceY)) return true;
    }
    return component.onMouseOut(x, y, differenceX, differenceY);
  }

  protected boolean sendTapEvent(GdxComponent component, float x, float y, int tapCount, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onTap(x, y, tapCount, pointer)) return true;
    }
    return component.onTap(x, y, tapCount, pointer);
  }

  protected boolean sendFlingEvent(GdxComponent component, float x, float y, float velocityX, float velocityY, int pointer) {
    for (GdxInputListener inputListener : component.getInputListeners()) {
      if (inputListener.onFling(x, y, velocityX, velocityY, pointer)) return true;
    }
    return component.onFling(x, y, velocityX, velocityY, pointer);
  }
  
  protected boolean propagateDragEvent(PointerRecord record, float x, float y, float differenceX, float differenceY, int pointer) {
    GdxComponent receiver = record.dragReceiver;
    if (!(receiver.isEnabled() && sendDragEvent(receiver, x, y, differenceX, differenceY, pointer))) {
      do {
        x += receiver.getX();
        y += receiver.getY();
        receiver = receiver.getContainer();
        if (receiver == null) return false;
      } while (!(receiver.isEnabled() && ((GdxContainer)receiver).onDragReceived(x, y, differenceX, differenceY, pointer)));
      if (record.dragReceiver != record.component) {
        ((GdxContainer)record.dragReceiver).onDragCapturingStopped(x,  y, pointer);
      }
      record.dragReceiver = (GdxContainer)receiver;
      return propagateDragEvent(record, x, y, differenceX, differenceY, pointer);
    }
    return true;
  }
  
  protected boolean propagateTapEvent(GdxComponent component, float x, float y, int tapCount, int pointer) {
    while (!(component.isEnabled() && sendTapEvent(component, x, y, tapCount, pointer))) {
      x += component.getX();
      y += component.getY();
      component = component.getContainer();
      if (component == null) return false;
    }
    return true;
  }
  
  protected boolean propagateFlingEvent(GdxComponent component, float x, float y, float velocityX, float velocityY, int pointer) {
    while (!(component.isEnabled() && sendFlingEvent(component, x, y, velocityX, velocityY, pointer))) {
      x += component.getX();
      y += component.getY();
      component = component.getContainer();
      if (component == null) return false;
    }
    return true;
  }
  
  public void clearInputs() {
    Gdx.app.log(TAG, "Clearing inputs");
    for (Iterator<Entry<Integer, PointerRecord>> it = pointers.entrySet().iterator(); it.hasNext(); ) {
      Entry<Integer, PointerRecord> entry = it.next();
      PointerRecord record = entry.getValue();
      GdxComponent component = record.component;
      if (component.isEnabled()) {
        float x = record.x - component.getFrameX();
        float y = record.y - component.getFrameY();
        sendTouchUpEvent(record, component, x, y, entry.getKey());
      }
      it.remove();
    }
    currentControl = null;
  }

  private class PointerRecord {
    public float x, y;
    public float previousX, previousY;
    public final float startX, startY;
    public boolean outside = false;
    public long time = System.nanoTime();
    public long previousTime = time;
    public final long startTime = time;
    public final GdxComponent component;
    public GdxComponent dragReceiver;
    public final Plane plane;
    
    public PointerRecord(Vector3 startPosition, GdxComponent component) {
      this.x = startPosition.x;
      this.y = startPosition.y;
      this.previousX = x;
      this.previousY = y;
      this.startX = x;
      this.startY = y;
      this.component = component;
      this.dragReceiver = component;
      plane = new Plane(new Vector3(0, 0, -1), startPosition.z);
    }

    public float getX() {
      return x;
    }

    public void setX(float x) {
      previousX = this.x;
      this.x = x;
    }

    public float getY() {
      return y;
    }

    public void setY(float y) {
      previousY = this.y;
      this.y = y;
    }

    public float getPreviousX() {
      return previousX;
    }

    public float getPreviousY() {
      return previousY;
    }

    public float getStartX() {
      return startX;
    }

    public float getStartY() {
      return startY;
    }

    public boolean isOutside() {
      return outside;
    }

    public void setOutside(boolean outside) {
      this.outside = outside;
    }
    
    public long getTime() {
      return time;
    }

    public void setTime(long time) {
      previousTime = this.time;
      this.time = time;
    }

    public long getPreviousTime() {
      return previousTime;
    }

    public long getStartTime() {
      return startTime;
    }

    public GdxComponent getComponent() {
      return component;
    }
    
    public Plane getPlane() {
      return plane;
    }

    @Override
    public String toString() {
      return PointerRecord.class.getSimpleName() +
          " ([x, y] = [" + x + ", " + y + "]" +
          ", [previousX, previousY] = " + previousX + ", " + previousY + "]" +
          ", [startX, startY] = " + startX + ", " + startY + "]" +
          ", outside = " + outside +
          ", time = " + time +
          ", previousTime = " + previousTime +
          ", startTime = " + startTime +
          ", component = " + component +
          ", plane = " + plane + ")";
    }
  }
  
  private class IntersectedComponent implements Comparable<IntersectedComponent> {
    public final GdxComponent component;
    public final Vector3 intersection;
    public final float distance;
    
    public IntersectedComponent(GdxComponent component, Vector3 intersection, float distance) {
      this.component = component;
      this.intersection = intersection;
      this.distance = distance;
    }

    @Override
    public int compareTo(IntersectedComponent other) {
      if (distance > other.distance) return 1;
      if (distance < other.distance) return -1;
      return 0;
    }

    @Override
    public String toString() {
      return IntersectedComponent.class.getSimpleName() +
          " (component = " + component +
          ", intersection = " + intersection +
          ", distance = " + distance + ")";
    }
  }

  private class TapCounter {
    private int tapCount;
    private long lastTapTime;
    private int lastNumberOfTouches;
    
    public TapCounter() {
      tapCount = 0;
      lastTapTime = 0;
      lastNumberOfTouches = 0;
    }
    
    public TapCounter(int numberOfTouches) {
      tapCount = 1;
      lastTapTime = System.nanoTime();
      lastNumberOfTouches = numberOfTouches;
    }
    
    public int getTapCount() {
      if (System.nanoTime() - lastTapTime > multitapMaxDelay) {
        tapCount = 0;
      }
      return tapCount;
    }
    
    public int countTap(int numberOfTouches) {
      long time = System.nanoTime();
      if (time - lastTapTime <= multitapMaxDelay && numberOfTouches == lastNumberOfTouches) {
        tapCount++;
      } else {
        tapCount = 1;
        lastNumberOfTouches = numberOfTouches;
      }
      lastTapTime = time;
      return tapCount;
    }
  }
}
