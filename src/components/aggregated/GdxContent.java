package components.aggregated;

import helpers.collections.DummyCollection;
import helpers.collections.SingleElementCollection;

import java.util.Collection;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

/**
 * Content holder to be used with containers which only hold a single component.
 * It automatically takes care of assigning a component in the container or removing it.
 *
 */
public class GdxContent {
  private static final Collection<GdxComponent> DUMMY_COLLECTION = new DummyCollection<GdxComponent>();
  
  private GdxContainer container;
  private GdxComponent content;
  private Collection<GdxComponent> collection = DUMMY_COLLECTION;
  
  /**
   * Creates a content holder for a given container.
   * @param container Container to use this content holder
   */
  public GdxContent(GdxContainer container) {
    this.container = container;
  }

  /**
   * @return Currently stored component or NULL
   */
  public GdxComponent get() {
    return content;
  }
  
  public Collection<GdxComponent> collection() {
    return collection;
  }
  
  /**
   * Component is stored in {@link GdxContent} and the container assigned to {@link GdxContent}
   * is set as it's container. If {@link GdxContent} already holds another component, it is
   * replaced but not disposed.
   * @param component Component to become a new content of assigned container
   */
  public void set(GdxComponent component) {
    GdxComponent previousContent = content;
    content = component;
    if (previousContent != null) previousContent.setContainer(null);
    if (component != null) {
      collection = SingleElementCollection.create(component);
      if (component.getContainer() != null) {
        component.getContainer().removeComponent(component);
      }
      component.setContainer(container);
    } else {
      collection = DUMMY_COLLECTION;
    }
  }

  public boolean is(GdxComponent component) {
    if (content == component) return true;
    if (content == null) return false;
    return content.equals(component);
  }

  /**
   * Removes currently stored component from the container and sets value to NULL
   * only if it matches the given component.
   * @param component Component to be tested for match
   * @return Whether component has been removed
   */
  public boolean remove(GdxComponent component) {
    if (content == component) {
      content = null;
      collection = DUMMY_COLLECTION;
      component.setContainer(null);
      return true;
    }
    return false;
  }

  /**
   * Removes and disposes currently stored component.
   */
  public void dispose() {
    GdxComponent component = content;
    set(null);
    component.dispose();
  }
}
