package helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureUtils {
  public static float scaleWidth(float height, TextureRegion image) {
    return height / image.getRegionHeight() * image.getRegionWidth();
  }
  
  public static float scaleHeight(float width, TextureRegion image) {
    return width / image.getRegionWidth() * image.getRegionHeight();
  }
}
