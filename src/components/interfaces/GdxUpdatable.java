package components.interfaces;

import io.GdxPainter;

import components.GdxMainFrame;

public interface GdxUpdatable {
  public GdxMainFrame getFrame();
  
  /**
   * By calling this method, re-rendering of the updatable ASAP
   * is requested. Otherwise, there is no assurance when or
   * if re-rendering will occure and therefore a
   * {@link GdxComponent#paint(float, float, GdxPainter) paint} 
   * methods called.
   * <p>
   * Also, only if an updatable became dirty prior to rendering,
   * its {@link #step(float) step} method will be called before
   * rendering happens.
   * <p>
   * Currently, there is no selective re-rendering implemented,
   * so the {@link GdxComponent#paint(float, float, GdxPainter) paint}
   * method is called every time anything changes in interface.
   */
  public void makeDirty();
  
  /**
   * This is called prior to {@link GdxComponent#paint(float, float, GdxPainter) paint}
   * method calls during frame rendering if the updatable reported itself dirty 
   * prior to that particular render. Any time dependent updates of the updatable 
   * are supposed to happen here.
   * <p>
   * Updatables needs to be able to repaint itself correctly even without prior
   * call of this method, since it may be requested to repaint even if it didn't 
   * reported itself dirty.
   * <p>
   * This method shouldn't be used for any updates independent of re-rendering or 
   * for time measurement, since it's executed as needed. It can be used for updates 
   * which can only occur during user input, though, since the updatable is continuously 
   * re-rendered while user interacts with it. Re-rendering can also be forced via 
   * continuous calls of {@link #makeDirty() makeDirty}, but that can lead to inefficiency.
   * @param delay Approximate amount of time passed since last call (in seconds)
   */
  public void step(float delay);
}
