package components.interfaces;


public interface GdxLayer extends GdxComponent {
  public void construct();
  
  public void activated();
  
  public void deactivated();
  
  public void hidden();
  
  public boolean transparent();
  
  public boolean neverDispose();
}
