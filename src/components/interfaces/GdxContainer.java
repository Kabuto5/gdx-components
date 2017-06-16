package components.interfaces;

import java.util.Collection;
import java.util.Iterator;

public interface GdxContainer extends GdxComponent {
  /**
   * Returns a collection (preferably not modifiable) of all
   * components currently in container. Subsequent changes 
   * of container's content may affect already returned 
   * collection, depending on implementation. Ordering also 
   * depends on implementation and may not be guaranteed.
   * @return Collection of all components in container
   */
  public Collection<GdxComponent> getComponents();

  /**
   * Tests if a given component is contained in the container.
   * @param component Component to be tested
   * @return Whether component is in container
   */
  public boolean hasComponent(GdxComponent component);
  
  /**
   * Removes a specific component instance from the container.
   * Removed component is not disposed and should be safe to reuse.
   * @param component Component instance to be removed
   * @return Whether the component has been found and removed
   */
  public boolean removeComponent(GdxComponent component);

  /**
   * Iterator returned by method should go through candidates for
   * interaction happening at specific coordinates, from the most
   * preffered to least. Container is not responsible for any
   * testing and providing all components is a valid approach.
   * <p>
   * Given coordinates may be used to narrow down the list, but
   * such operation is likely to be cost effective only if it has
   * lower than O(n) time complexity.
   * @param x X-coordinate relative to position of the container
   * @param y Y-coordinate relative to position of the container
   * @return Iterator going through a list of candidates for 
   *         interaction at specific coordinates
   */
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y);

  /**
   * Is called whenever a component in the container changed its size,
   * so the container can rearrange it's content or perform other
   * actions accordingly.
   * @param component Component which's size changed
   */
  public void reportResize(GdxComponent component);

  public boolean onDragReceived(float x, float y, float differenceX, float differenceY, int pointer);

  public void onDragCapturingStopped(float x, float y, int pointer);
}