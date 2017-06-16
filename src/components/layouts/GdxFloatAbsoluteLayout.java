package components.layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

public class GdxFloatAbsoluteLayout extends GdxAbsoluteLayout {
  private List<AlignedComponent> alignedComponents = new ArrayList<AlignedComponent>();
  
  public void setAlignedComponent(GdxComponent component, float u, float v) {
    boolean contained = false;
    for (AlignedComponent aligned : alignedComponents) {
      contained = contained || aligned.component.equals(component);
    }
    if (!contained) {
      alignedComponents.add(new AlignedComponent(component, u, v));
      GdxContainer container = component.getContainer();
      if (container != null) alignComponents(container);
    }
  }
  
  public void clearAlignedComponents() {
    alignedComponents.clear();
  }
  
  @Override
  public void alignComponents(GdxContainer container) {
    float width = container.getWidth();
    float height = container.getHeight();
    for (Iterator<AlignedComponent> it = alignedComponents.iterator(); it.hasNext(); ) {
      AlignedComponent aligned = it.next();
      if (container.hasComponent(aligned.component)) {
        aligned.component.setLocation(
            aligned.u * width - aligned.component.getWidth() * aligned.u, 
            aligned.v * height - aligned.component.getHeight() * aligned.v);
      } else {
        it.remove();
      }
    }
  }
  
  private static class AlignedComponent {
    public GdxComponent component;
    public float u, v;
    
    public AlignedComponent(GdxComponent component, float u, float v) {
      this.component = component;
      this.u = u;
      this.v = v;
    }
  }
}
