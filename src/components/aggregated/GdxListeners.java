package components.aggregated;

import general.interfaces.GdxListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A holder for listeners which automatically takes care of adding and removing them properly.
 * @param <T> Class of listeners stored in this structure
 *
 */
public class GdxListeners<T extends GdxListener> implements Iterable<T> {
  private final List<T> listeners;
  private final List<Object> tags;
  private final Collection<T> unmodifiableListeners;
  
  public GdxListeners() {
    this(1);
  }
  
  public GdxListeners(int initialCapacity) {
    listeners = new ArrayList<T>(initialCapacity);
    tags = new ArrayList<Object>(initialCapacity);
    unmodifiableListeners = Collections.unmodifiableCollection(listeners);
  }
  
  /**
   * Adds a new listener unless it already exists, with a tag
   * to identify it later. If NULL value is supplied as a tag,
   * listener is added as untagged listener and cannot be
   * identified by tag later.
   * @param listenerTag Tag to be assigned to the listener
   * @param listener Listener to be added
   * @return Whether the listener has been added
   */
  public boolean add(Object listenerTag, T listener) {
    /* Don't add duplicate listener */
    if (listeners.contains(listener)) return false;
    /* Look for matching tag already existing in the list unless tag is NULL */
    if (listenerTag != null) {
      int size = tags.size();
      for (int i = 0; i < size; i++) {
        Object tag = tags.get(i);
        /* Replace listener with matching tag */
        if (listenerTag.equals(tag)) {
          listeners.remove(i);
          listeners.add(i, listener);
          return true;
        }
      }
    }
    /* Add new listener if tag was NULL or no matching tag was found */
    listeners.add(listener);
    tags.add(listenerTag);
    return true;
  }

  public boolean remove(T listener) {
    int index = listeners.indexOf(listener);
    if (index >= 0) {
      listeners.remove(index);
      tags.remove(index);
      return true;
    }
    return false;
  }
  
  public T remove(Object listenerTag) {
    if (listenerTag == null) throw new NullPointerException("Null value can not be used as a tag");
    int index = tags.indexOf(listenerTag);
    if (index >= 0) {
      T listener = listeners.remove(index);
      tags.remove(index);
      return listener;
    }
    return null;
  }
  
  public void clear() {
    listeners.clear();
    tags.clear();
  }
  
  public T get(Object listenerTag) {
    if (listenerTag == null) throw new NullPointerException("Null value can not be used as a tag");
    int index = tags.indexOf(listenerTag);
    if (index >= 0) {
      return listeners.get(index);
    }
    return null;
  }
  
  public Collection<T> getAll() {
    return unmodifiableListeners;
  }

  @Override
  public Iterator<T> iterator() {
    return unmodifiableListeners.iterator();
  }
}
