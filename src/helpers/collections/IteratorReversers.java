package helpers.collections;

import java.util.ArrayList;
import java.util.Iterator;

public class IteratorReversers {
  public static <T> Iterator<T> createListIteratorReverser(ArrayList<T> list) {
    return new ListIteratorReverser<T>(list.listIterator(list.size()));
  }
  
  public static <T> Iterator<T> createImmutableListIteratorReverser(ArrayList<T> list) {
    return new ImmutableListIteratorReverser<T>(list.listIterator(list.size()));
  }
}
