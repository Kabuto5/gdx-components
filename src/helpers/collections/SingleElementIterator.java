package helpers.collections;

import java.util.Iterator;

/**
 * An iterator returning only a single element before it become exhausted.
 *
 * @param <E> Type of element returned by the iterator
 */
public class SingleElementIterator<E> implements Iterator<E> {
  private E element;
  private boolean unused;

  public SingleElementIterator(E element) {
    this.element = element;
    this.unused = true;
  }
  
  @Override
  public boolean hasNext() {
    return unused;
  }

  @Override
  public E next() {
    unused = false;
    return element;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("This iterator is not backed by proper collection to modify");
  }
  
  /**
   * Returns a new instance of iterator returning a given single element.
   * @param element Element to be returned by the iterator
   * @return New instance of iterator returning a single element
   */
  public static <E> SingleElementIterator<E> create(E element) {
    return new SingleElementIterator<E>(element);
  }
}
