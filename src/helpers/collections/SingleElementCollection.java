package helpers.collections;

import java.util.AbstractCollection;
import java.util.Iterator;

public class SingleElementCollection<E> extends AbstractCollection<E> {
  private final E element;
  
  public SingleElementCollection(E element) {
    this.element = element;
  }
  
  @Override
  public int size() {
    return 1;
  }

  @Override
  public Iterator<E> iterator() {
    return new SingleElementIterator<E>(element);
  }
  
  public static <E> SingleElementCollection<E> create(E element) {
    return new SingleElementCollection<E>(element);
  }
}
