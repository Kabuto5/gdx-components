package helpers.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A dummy iterator which returns nothing. Always.
 *
 * @param <E> Type of element this iterator pretends to return
 */
public class DummyIterator<E> implements Iterator<E> {
  @SuppressWarnings("rawtypes")
  private static final DummyIterator instance = new DummyIterator();
  
  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public E next() {
    throw new NoSuchElementException("This is a dummy iterator which never returns anything");
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("This iterator is not backed by a proper collection to modify");
  }
  
  /**
   * Returns a generic singleton instance of iterator casted to a given type.
   * @param type Type which will be used to cast the iterator
   * @return Iterator typecasted to a given type
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyIterator<T> create(Class<T> type) {
    return (DummyIterator<T>)instance;
  }
  
  /**
   * Returns a generic singleton instance of iterator casted to a type of given object.
   * @param element Object which's type will be used for the iterator
   * @return Iterator typecasted according to a given object
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyIterator<T> create(T element) {
    return (DummyIterator<T>)instance;
  }
  
  /**
   * Returns a generic singleton instance of iterator casted according to a given collection.
   * @param collection Collection which's elements' type will be used for the iterator
   * @return Iterator typecasted according to a given collection
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyIterator<T> create(Collection<T> collection) {
    return (DummyIterator<T>)instance;
  }
}
