package components.abstracts;

import com.badlogic.gdx.math.Vector2;
import components.interfaces.GdxSingletouchComponent;

public abstract class GdxSingletouchActiveComponent extends GdxAbstractComponent implements GdxSingletouchComponent {
  private float pointerX, pointerY;
  
  public GdxSingletouchActiveComponent(float x, float y, float width, float height) {
    super(x, y, width, height);
    makeActive();
    clearPointerLocation();
  }
  
  private void clearPointerLocation() {
    pointerX = Float.NaN;
    pointerY = Float.NaN;
  }
  
  protected float getPointerX() {
    return pointerX;
  }
  
  protected float getPointerY() {
    return pointerY;
  }
  
  protected Vector2 getPointerLocation() {
    return new Vector2(pointerX, pointerY);
  }

  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    boolean handled = super.onTouchDown(x, y, pointer);
    pointerX = x;
    pointerY = y;
    return handled;
  }

  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    boolean handled = super.onTouchUp(x, y, pointer);
    clearPointerLocation();
    return handled;
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    boolean handled = super.onDrag(x, y, differenceX, differenceY, pointer);
    pointerX = x;
    pointerY = y;
    return handled;
  }
}
