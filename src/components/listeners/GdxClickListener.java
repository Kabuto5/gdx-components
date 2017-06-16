package components.listeners;

import components.interfaces.GdxComponent;

public interface GdxClickListener extends GdxComponentListener {
  public void onClick(GdxComponent sender);
}