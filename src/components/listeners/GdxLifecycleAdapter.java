package components.listeners;

import components.GdxMainFrame;

public abstract class GdxLifecycleAdapter implements GdxLifecycleListener {
  @Override
  public void onCreate(GdxMainFrame sender) { }

  @Override
  public void onDispose(GdxMainFrame sender) { }

  @Override
  public void onPause(GdxMainFrame sender) { }

  @Override
  public void onResize(GdxMainFrame sender, int width, int height) { }

  @Override
  public void onResume(GdxMainFrame sender) { }
}