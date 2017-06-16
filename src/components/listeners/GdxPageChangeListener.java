package components.listeners;

import components.interfaces.GdxComponent;

public interface GdxPageChangeListener extends GdxComponentListener {
  public void onPageChange(GdxComponent sender, int pageIndex);

  public void onPageSettle(GdxComponent sender, int pageIndex);
}
