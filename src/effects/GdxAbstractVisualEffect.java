package effects;

import io.GdxPainter;

import components.GdxMainFrame;
import components.interfaces.GdxComponent;

public abstract class GdxAbstractVisualEffect implements GdxVisualEffect {
  private GdxComponent component;
  
  public GdxAbstractVisualEffect(GdxComponent component) {
    this.component = component;
  }

  @Override
  public GdxComponent getComponent() {
    return component;
  }

  @Override
  public void before(float x, float y, GdxPainter painter) { }

  @Override
  public void after(float x, float y, GdxPainter painter) { }
  
  @Override
  public GdxMainFrame getFrame() {
    return component.getFrame();
  }

  @Override
  public void makeDirty() {
    GdxMainFrame frame = getFrame();
    if (frame != null) frame.reportDirty(this);
  }

  @Override
  public void step(float delay) { }
  
  @Override
  public void dispose() { }

  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    return false;
  }
  
  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    return false;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  @Override
  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  @Override
  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  @Override
  public boolean onMouseMove(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  @Override
  public boolean onMouseOver(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  @Override
  public boolean onMouseOut(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  @Override
  public boolean onTap(float x, float y, int tapCount, int pointer) {
    return false;
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    return false;
  }
}
