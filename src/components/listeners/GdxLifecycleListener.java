package components.listeners;

import components.GdxMainFrame;

public interface GdxLifecycleListener extends GdxComponentListener {
  public void onCreate(GdxMainFrame sender);
  
  public void onDispose(GdxMainFrame sender);
  
  public void onPause(GdxMainFrame sender);
  
  public void onResize(GdxMainFrame sender, int width, int height);
  
  public void onResume(GdxMainFrame sender);
}