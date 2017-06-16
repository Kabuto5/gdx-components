package helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;
import components.interfaces.GdxDragDropComponent;
import components.interfaces.GdxDragTarget;

import effects.GdxVisualEffect;

public class ComponentUtils {
  public static void makeAllDirty(GdxComponent component) {
    component.makeDirty();
    if (component instanceof GdxContainer) {
      LinkedList<GdxComponent> content = new LinkedList<GdxComponent>();
      content.addAll(((GdxContainer)component).getComponents());
      while (!content.isEmpty()) {
        component = content.removeFirst();
        component.makeDirty();
        Collection<GdxVisualEffect> visualEffects = component.getVisualEffects();
        for (GdxVisualEffect visualEffect : visualEffects) {
          visualEffect.makeDirty();
        }
        if (component instanceof GdxContainer) {
          content.addAll(((GdxContainer)component).getComponents());
        }
      }
    }
  }
  
  /**
   * Finds a drag target for component if it exists. Target must lie at given 
   * coordinates and have to accept the component. Targets deeper within component
   * structure are preffered.
   * <p>
   * This method is likely to be useful for any more or less standardly behaving
   * implementation of draggable component.
   * @param component Component to find target for
   * @param x Coordinate to look for target at, relative to frame
   * @param y Coordinate to look for target at, relative to frame
   * @return Drag target for a component, or NULL if there is none.
   */
  public static GdxComponent findDragTarget(GdxDragDropComponent component, float x, float y) {
    return ComponentUtils.findDragTarget(component, component, x - component.getFrameX(), y - component.getFrameY());
  }
  
  /**
   * Searchs for a drag target within given container. Target must lie at given 
   * coordinates and have to accept the component. Targets deeper within component
   * structure are preffered.
   * @param draggable Component to find target for
   * @param container Container to be searched
   * @param x Coordinate relative to container's position within frame
   * @param y Coordinate relative to container's position within frame
   * @return Drag target for a component, or NULL if none is found.
   */
  private static GdxComponent findDragTargetWithinContainer(GdxDragDropComponent draggable, GdxContainer container, float x, float y) {
    for (Iterator<GdxComponent> it = container.interactionCandidatesIterator(x, y); it.hasNext(); ) {
      GdxComponent component = it.next();
      float componentX = x - component.getX();
      float componentY = y - component.getY();
      if (component.isEnabled() && component.insideActiveArea(componentX, componentY)) {
        //If the component is a container, its children are tested first
        if (component instanceof GdxContainer) {
          GdxComponent dragTarget = ComponentUtils.findDragTargetWithinContainer(draggable, (GdxContainer)component, componentX, componentY);
          if (dragTarget != null) return dragTarget;
        }
        //If any children wasn't suitable as target or component isn't a container,
        //component itself is tested.
        if (component instanceof GdxDragTarget && ((GdxDragTarget)component).accept(draggable)) {
          return component;
        }
      }
    }
    return null;
  }
  
  /**
   * Finds a drag target for component if it exists. Target must lie at given 
   * coordinates and have to accept the component. Targets deeper within component
   * structure are preffered.
   * @param draggable Component to find target for
   * @param origin Origin of the search - dragged component for the initial call
   *               or a container for subsequent calls.
   * @param x Coordinate relative to component's position within frame
   * @param y Coordinate relative to component's position within frame
   * @return Drag target for a component, or NULL if there is none.
   */
  private static GdxComponent findDragTarget(GdxDragDropComponent draggable, GdxComponent origin, float x, float y) {
    GdxContainer container = origin.getContainer();
    if (container == null) {
      return null;
    }
    x += origin.getX();
    y += origin.getY();
    if (container.insideActiveArea(x, y)) {
      for (Iterator<GdxComponent> it = container.interactionCandidatesIterator(x, y); it.hasNext(); ) {
        GdxComponent component = it.next();
        if (component == origin) continue;
        float componentX = x - component.getX();
        float componentY = y - component.getY();
        if (component.isEnabled() && component.insideActiveArea(componentX, componentY)) {
          //If the component is a container, its children are tested first
          if (component instanceof GdxContainer) {
            GdxComponent dragTarget = findDragTargetWithinContainer(draggable, (GdxContainer)component, componentX, componentY);
            if (dragTarget != null) return dragTarget;
          }
          //If any children wasn't suitable as target or component isn't a container,
          //component itself is tested.
          if (component instanceof GdxDragTarget && ((GdxDragTarget)component).accept(draggable)) {
            return component;
          }
        }
      }
      if (container instanceof GdxDragTarget && ((GdxDragTarget)container).accept(draggable)) {
        return container;
      }
    }
    //If no target was found within current container, look outside
    return findDragTarget(draggable, container, x, y);
  }
}
