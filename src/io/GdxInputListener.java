package io;

import general.interfaces.GdxListener;

public interface GdxInputListener extends GdxListener {
  public boolean onTouchDown(float x, float y, int pointer);
  
  public boolean onTouchUp(float x, float y, int pointer);

  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer);

  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer);

  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer);

  public boolean onMouseMove(float x, float y, float differenceX, float differenceY);

  public boolean onMouseOver(float x, float y, float differenceX, float differenceY);

  public boolean onMouseOut(float x, float y, float differenceX, float differenceY);

  public boolean onTap(float x, float y, int tapCount, int pointer);

  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer);
}
