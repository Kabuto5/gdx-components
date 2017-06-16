package helpers.collections;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Simple wrapper class providing a descending iterator for
 * {@link java.util.ArrayList ArrayList}, compliant with
 * standard {@link java.util.Iterator Iterator} interface.
 *
 * @param <T> The type of elements returned by this iterator
 */
public class ListIteratorReverser<T> implements Iterator<T> {
  private final ListIterator<T> iterator;
  
  public ListIteratorReverser(ListIterator<T> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasPrevious();
  }

  @Override
  public T next() {
    return iterator.previous();
  }

  @Override
  public void remove() {
    iterator.remove();
  }
}
