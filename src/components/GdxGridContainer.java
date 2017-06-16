package components;

import helpers.collections.DummyIterator;
import helpers.collections.SingleElementIterator;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.math.Vector2;
import components.abstracts.GdxAbstractContainer;
import components.interfaces.GdxComponent;

public class GdxGridContainer extends GdxAbstractContainer {
  private GdxComponent[] components;
  private Collection<GdxComponent> unmodifiableComponents = new UnmodifiableComponentArray();
  private int columnCount, rowCount;
  
  public GdxGridContainer(float x, float y, float width, float height, int columnCount, int rowCount) {
    super(x, y, width, height);
    components = new GdxComponent[columnCount * rowCount];
    this.columnCount = columnCount;
    this.rowCount = rowCount;
  }

  public GdxGridContainer(float x, float y, Vector2 cellSize, int columnCount, int rowCount) {
    this(x, y, columnCount * cellSize.x, rowCount * cellSize.y, columnCount, rowCount);
  }

  public int getColumnCount() {
    return columnCount;
  }

  public int getRowCount() {
    return rowCount;
  }

  private void updateComponentLocation(int column, int row) {
    GdxComponent component = components[row * columnCount + column];
    if (component != null) {
      float cellWidth = getWidth() / columnCount;
      float cellHeight = getHeight() / rowCount;
      component.setLocation(
          column * cellWidth + 0.5f * (cellWidth - component.getWidth()), 
          row * cellHeight + 0.5f * (cellHeight - component.getHeight()));
    }
  }
  
  private void updateComponentLocations() {
    for (int row = 0; row < rowCount; row++) {
      for (int column = 0; column < columnCount; column++) {
        updateComponentLocation(column, row);
      }
    }
  }
  
  public void putComponent(int column, int row, GdxComponent component) {
    if (component.getContainer() != null) {
      component.getContainer().removeComponent(component);
    }
    components[row * columnCount + column] = component;
    component.setContainer(this);
    updateComponentLocation(column, row);
  }
  
  public GdxComponent getComponent(int column, int row) {
    return components[row * columnCount + column];
  }

  @Override
  public Collection<GdxComponent> getComponents() {
    return unmodifiableComponents;
  }
  
  public GdxComponent removeComponent(int column, int row) {
    GdxComponent component = components[row * columnCount + column];
    if (component != null) {
      components[row * columnCount + column] = null;
      component.setContainer(null);
    }
    return component;
  }
  
  @Override
  public boolean removeComponent(GdxComponent component) {
    for (int i = 0; i < components.length; i++) {
      if (components[i] == component) {
        components[i] = null;
        component.setContainer(null);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasComponent(GdxComponent component) {
    for (int i = 0; i < components.length; i++) {
      if (components[i] == component) return true;
    }
    return false;
  }

  @Override
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y) {
    int column = (int)(x * columnCount / getWidth());
    int row = (int)(y * rowCount / getHeight());
    GdxComponent candidate = components[row * columnCount + column];
    if (candidate == null) {
      return DummyIterator.create(candidate);
    } else {
      return SingleElementIterator.create(candidate);
    }
  }
  
  @Override
  protected void resized() {
    super.resized();
    updateComponentLocations();
  }

  @Override
  public void reportResize(GdxComponent component) {
    for (int i = 0; i < components.length; i++) {
      if (components[i] == component) {
        updateComponentLocation(i % columnCount, i / columnCount);
      }
    }
  }
  
  @Override
  public void dispose() {
    for (int i = 0; i < components.length; i++) {
      GdxComponent component = components[i];
      if (component != null) {
        components[i] = null;
        component.setContainer(null);
        component.dispose();
      }
    }
    super.dispose();
  }
  
  private class UnmodifiableComponentArray extends AbstractCollection<GdxComponent> {
    @Override
    public Iterator<GdxComponent> iterator() {
      return new UnmodifiableComponentArrayIterator();
    }

    @Override
    public int size() {
      int count = 0;
      for (int i = 0; i < components.length; i++) {
        if (components[i] != null) count++;
      }
      return count;
    }
  }
  
  private class UnmodifiableComponentArrayIterator implements Iterator<GdxComponent> {
    private int index;
    
    public UnmodifiableComponentArrayIterator() {
      index = 0;
      while (index < components.length && components[index] == null) {
        index++;
      }
    }
    
    @Override
    public boolean hasNext() {
      return index < components.length;
    }

    @Override
    public GdxComponent next() {
      if (hasNext()) {
        GdxComponent element = components[index++];
        while (index < components.length && components[index] == null) {
          index++;
        }
        return element;
      }
      throw new NoSuchElementException("No more elements in collection");
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("This iterator can not modify its underlying collection");
    }
  }
}
