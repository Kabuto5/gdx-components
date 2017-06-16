package helpers.collections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class DummyList<E> extends DummyCollection<E> implements List<E> {
  @Override
  public ListIterator<E> listIterator() {
    return DummyListIterator.create(this);
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    if (index < 0 || index > size()) throw new IndexOutOfBoundsException();
    return DummyListIterator.create(this);
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    throw new UnsupportedOperationException("This is a dummy list which can not contain anything");
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> collection) {
    throw new UnsupportedOperationException("This is a dummy list which can not contain anything");
  }

  @Override
  public boolean add(E element) {
    throw new UnsupportedOperationException("This is a dummy list which can not contain anything");
  }

  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException("This is a dummy list which can not contain anything");
  }

  @Override
  public E set(int index, E element) {
    throw new UnsupportedOperationException("This is a dummy list which can not contain anything");
  }
  
  @Override
  public E get(int index) {
    throw new IndexOutOfBoundsException("This is a dummy list which is always empty");
  }

  @Override
  public int indexOf(Object object) {
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    return -1;
  }

  @Override
  public E remove(int index) {
    throw new IndexOutOfBoundsException("This is a dummy list which is always empty");
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) throw new IndexOutOfBoundsException();
    return this;
  }
}
