package components.listeners;

import components.interfaces.GdxComponent;

public interface GdxDragDropListener extends GdxComponentListener {
  public void onStartDrag(GdxComponent sender);
  
  public void onDragAbort(GdxComponent sender);
  
  public void onDragDrop(GdxComponent sender, GdxComponent target, float dropX, float dropY);
}