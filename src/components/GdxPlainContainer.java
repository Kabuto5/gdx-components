package components;

import helpers.collections.IteratorReversers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import components.abstracts.GdxAbstractContainer;
import components.interfaces.GdxComponent;
import components.layouts.GdxAbsoluteLayout;
import components.layouts.GdxLayout;

public class GdxPlainContainer extends GdxAbstractContainer {
  private final ArrayList<GdxComponent> components = new ArrayList<GdxComponent>();
  private final Collection<GdxComponent> unmodifiableComponents = Collections.unmodifiableList(components);
  private GdxLayout layout;

  public GdxPlainContainer(float x, float y, float width, float height, GdxLayout layout) {
    super(x, y, width, height);
    setLayout(layout);
  }
  
  public GdxPlainContainer(float x, float y, float width, float height) {
    this(x, y, width, height, new GdxAbsoluteLayout());
  }

  public void addComponent(GdxComponent component) {
    insertComponent(components.size(), component);
  }

  public void addComponents(GdxComponent... components) {
    for (GdxComponent component : components) {
      addComponent(component);
    }
  }
  
  public void insertComponent(int index, GdxComponent component) {
    if (component.getContainer() != null) {
      component.getContainer().removeComponent(component);
    }
    components.add(index, component);
    component.setContainer(this);
    componentSetChanged(component, false);
  }

  @Override
  public Collection<GdxComponent> getComponents() {
    return unmodifiableComponents;
  }

  public GdxComponent getComponent(int index) {
    return components.get(index);
  }
  
  public int getComponentCount() {
    return components.size();
  }
  
  public boolean isEmpty() {
    return components.isEmpty();
  }
  
  /**
   * Returns an index of a given component in container,
   * or -1 if component is not in the container.
   * @param component Component its index if to be found
   * @return Index of the component or -1
   */
  public int indexOf(GdxComponent component) {
    return components.indexOf(component);
  }

  public boolean hasComponent(GdxComponent component) {
    for (GdxComponent containedComponent : components) {
      if (containedComponent == component) return true;
    }
    return false;
  }

  public boolean removeComponent(GdxComponent component) {
    for (Iterator<GdxComponent> it = components.iterator(); it.hasNext(); ) {
      if (it.next() == component) {
        it.remove();
        component.setContainer(null);
        componentSetChanged(component, true);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Removes a component at specific position from the container
   * and returns it. Removed component is not disposed and
   * should be safe to reuse.
   * @param index Position of the component to be removed
   * @return The removed component
   */
  public GdxComponent removeComponent(int index) {
    GdxComponent component = components.remove(index);
    component.setContainer(null);
    componentSetChanged(component, true);
    return component;
  }
  
  /**
   * Removes all components from the container.
   * Components can be disposed in the process optionally.
   * @param dispose Whether to dispose components
   */
  public void clearComponents(boolean dispose) {
    for (Iterator<GdxComponent> it = components.iterator(); it.hasNext(); ) {
      GdxComponent component = it.next();
      it.remove();
      component.setContainer(null);
      if (dispose) component.dispose();
    }
    componentSetChanged();
  }
  
  /**
   * Removes all components from the container.
   * Components are disposed in the process automatically.
   */
  public void clearComponents() {
    clearComponents(true);
  }

  @Override
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y) {
    return IteratorReversers.createImmutableListIteratorReverser(components);
  }
  
  @Override
  protected void resized() {
    super.resized();
    layout.alignComponents(this);
  }

  @Override
  public void reportResize(GdxComponent component) {
    layout.alignComponents(this);
  }

  protected void componentSetChanged() {
    layout.alignComponents(this);
  }

  protected void componentSetChanged(GdxComponent component, boolean removed) {
    componentSetChanged();
  }
  
  public GdxLayout getLayout() {
    return layout;
  }
  
  public void setLayout(GdxLayout layout) {
    this.layout = layout;
    layout.alignComponents(this);
  }
  
  @Override
  public void dispose() {
    for (Iterator<GdxComponent> it = components.iterator(); it.hasNext(); ) {
      GdxComponent component = it.next();
      it.remove();
      component.setContainer(null);
      component.dispose();
    }
    super.dispose();
  }
}
