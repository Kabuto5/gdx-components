package io;

public abstract class GdxInputAdapter implements GdxInputListener {
  public boolean onTouchDown(float x, float y, int pointer) {
    return false;
  }
  
  public boolean onTouchUp(float x, float y, int pointer) {
    return false;
  }

  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer) {
    return false;
  }

  public boolean onMouseMove(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  public boolean onMouseOver(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  public boolean onMouseOut(float x, float y, float differenceX, float differenceY) {
    return false;
  }

  public boolean onTap(float x, float y, int tapCount, int pointer) {
    return false;
  }

  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    return false;
  }
}
