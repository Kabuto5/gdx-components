package helpers.collections;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class DummyListIterator<E> extends DummyIterator<E> implements ListIterator<E> {
  @SuppressWarnings("rawtypes")
  private static final DummyIterator instance = new DummyIterator();

  @Override
  public boolean hasPrevious() {
    return false;
  }

  @Override
  public E previous() {
    throw new NoSuchElementException("This is a dummy iterator which never returns anything");
  }

  @Override
  public int nextIndex() {
    return 0;
  }

  @Override
  public int previousIndex() {
    return -1;
  }

  @Override
  public void set(E e) {
    throw new UnsupportedOperationException("This iterator is not backed by a proper collection to modify");
  }

  @Override
  public void add(E e) {
    throw new UnsupportedOperationException("This iterator is not backed by a proper collection to modify");
  }
  
  /**
   * Returns a generic singleton instance of iterator casted to a given type.
   * @param type Type which will be used to cast the iterator
   * @return Iterator typecasted to a given type
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyListIterator<T> create(Class<T> type) {
    return (DummyListIterator<T>)instance;
  }
  
  /**
   * Returns a generic singleton instance of iterator casted to a type of given object.
   * @param element Object which's type will be used for the iterator
   * @return Iterator typecasted according to a given object
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyListIterator<T> create(T element) {
    return (DummyListIterator<T>)instance;
  }
  
  /**
   * Returns a generic singleton instance of iterator casted according to a given list.
   * @param list List which's elements' type will be used for the iterator
   * @return Iterator typecasted according to a given list
   */
  @SuppressWarnings("unchecked")
  public static <T> DummyListIterator<T> create(List<T> list) {
    return (DummyListIterator<T>)instance;
  }
}
