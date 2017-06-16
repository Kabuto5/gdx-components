package io;

import com.badlogic.gdx.graphics.Camera;

public abstract class GdxSpecialCamera {
  private final Camera camera;
  
  public GdxSpecialCamera(Camera camera) {
    this.camera = camera;
  }
  
  public Camera getCamera() {
    return camera;
  }
  
  public abstract void onResize(float canvasWidth, float canvasHeight, int screenWidth, int screenHeight, float zoom);
}
