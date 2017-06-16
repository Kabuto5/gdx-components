package components.interfaces;


public interface GdxDragTarget {
  public boolean accept(GdxComponent component);
  
  public void onDragOver(GdxComponent component);
  
  public void onDragOut(GdxComponent component);
  
  public void onDragDrop(GdxComponent component, float dropX, float dropY);
}
