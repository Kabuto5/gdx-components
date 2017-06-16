package helpers.collections;

import java.util.Collection;
import java.util.Iterator;

public class DummyCollection<E> implements Collection<E> {
  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean contains(Object object) {
    return false;
  }

  @Override
  public Iterator<E> iterator() {
    return DummyIterator.create(this);
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] array) {
    return (T[])new Object[0];
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    throw new UnsupportedOperationException("This is a dummy collection which can not contain anything");
  }

  @Override
  public boolean add(E element) {
    throw new UnsupportedOperationException("This is a dummy collection which can not contain anything");
  }

  @Override
  public boolean remove(Object object) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  @Override
  public void clear() { }
}
