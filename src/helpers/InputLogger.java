package helpers;

import com.badlogic.gdx.Gdx;

import io.GdxInputListener;

public class InputLogger implements GdxInputListener {
  private final String tag;
  
  public InputLogger(String tag) {
    this.tag = tag;
  }
  
  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    Gdx.app.log(tag, "touchDown: x = " + x + ", y = " + y + ", pointer = " + pointer);
    return false;
  }

  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    Gdx.app.log(tag, "touchUp: x = " + x + ", y = " + y + ", pointer = " + pointer);
    return false;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    Gdx.app.log(tag, "drag: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY + 
        ", pointer = " + pointer);
    return false;
  }

  @Override
  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer) {
    Gdx.app.log(tag, "dragIn: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY + 
        ", pointer = " + pointer);
    return false;
  }

  @Override
  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer) {
    Gdx.app.log(tag, "dragOut: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY + 
        ", pointer = " + pointer);
    return false;
  }

  public boolean onMouseMove(float x, float y, float differenceX, float differenceY) {
    Gdx.app.log(tag, "mouseMove: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY);
    return false;
  }

  public boolean onMouseOver(float x, float y, float differenceX, float differenceY) {
    Gdx.app.log(tag, "mouseOver: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY);
    return false;
  }

  public boolean onMouseOut(float x, float y, float differenceX, float differenceY) {
    Gdx.app.log(tag, "mouseOut: x = " + x + ", y = " + y + 
        ", differenceX = " + differenceX + ", differenceY = " + differenceY);
    return false;
  }

  @Override
  public boolean onTap(float x, float y, int tapCount, int pointer) {
    Gdx.app.log(tag, "tap: x = " + x + ", y = " + y + 
        ", tapCount = " + tapCount + ", pointer = " + pointer);
    return false;
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    Gdx.app.log(tag, "fling: x = " + x + ", y = " + y + 
        ", velocityX = " + velocityX + ", velocityY = " + velocityY + 
        ", pointer = " + pointer);
    return false;
  }
}
