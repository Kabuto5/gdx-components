package components;

import helpers.collections.DummyIterator;
import helpers.collections.SingleElementIterator;
import io.GdxPainter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.badlogic.gdx.math.Rectangle;
import components.abstracts.GdxAbstractContainer;
import components.aggregated.GdxListeners;
import components.interfaces.GdxComponent;
import components.interfaces.GdxLayer;
import components.listeners.GdxActiveLayerChangeListener;

/**
 * This class is designed to help break complex menus, consisting of multiple screens,
 * into individual layers and to manage switching between them. It also supports on-demand
 * layer construction and disposal of recently unused ones in order to save resources.
 *
 */
public class GdxLayerContainer extends GdxAbstractContainer {
  private HashMap<Integer, GdxLayer> layers = new HashMap<Integer, GdxLayer>();
  private Collection<GdxComponent> unmodifiableComponents = 
      Collections.<GdxComponent>unmodifiableCollection(layers.values());
  private GdxListeners<GdxActiveLayerChangeListener> activeLayerChangeListeners = 
      new GdxListeners<GdxActiveLayerChangeListener>();
  private LinkedList<GdxLayer> constructedLayers = new LinkedList<GdxLayer>();
  private LinkedList<GdxLayer> visibleLayers = new LinkedList<GdxLayer>();
  private int constructedLayerLimit;
  
  public GdxLayerContainer(float x, float y, float width, float height, int constructedLayerLimit) {
    super(x, y, width, height);
    this.constructedLayerLimit = constructedLayerLimit;
  }
  
  public GdxLayerContainer(float x, float y, float width, float height) {
    this(x, y, width, height, Integer.MAX_VALUE);
  }

  public void putLayer(int id, GdxLayer layer) {
    if (layer.getContainer() != null) {
      layer.getContainer().removeComponent(layer);
    }
    removeLayer(id);
    layers.put(id, layer);
    layer.setContainer(this);
  }

  @Override
  public Collection<GdxComponent> getComponents() {
    return unmodifiableComponents;
  }
  
  @Override
  public boolean hasComponent(GdxComponent component) {
    return layers.values().contains(component);
  }

  public GdxLayer getLayer(int id) {
    return layers.get(id);
  }
  
  private void layerRemoved(GdxLayer layer) {
    if (layer == getActiveLayer()) {
      visibleLayers.removeFirst();
      layer.deactivated();
      if (!visibleLayers.isEmpty())
        visibleLayers.getFirst().activated();
    }
    else if (visibleLayers.remove(layer)) {
      layer.hidden();
    }
    constructedLayers.remove(layer);
    layer.setContainer(null);
  }
  
  public boolean removeLayer(GdxLayer layer) {
    for (Iterator<Entry<Integer, GdxLayer>> it = layers.entrySet().iterator(); it.hasNext(); ) {
      if (it.next().getValue() == layer) {
        it.remove();
        layerRemoved(layer);
      }
    }
    return false;
  }
  
  @Override
  public boolean removeComponent(GdxComponent component) {
    if (component instanceof GdxLayer) {
      return removeLayer((GdxLayer)component);
    }
    return false;
  }
  
  public GdxLayer removeLayer(int id) {
    GdxLayer layer = layers.remove(id);
    if (layer != null) {
      layerRemoved(layer);
    }
    return layer;
  }
  
  public void clearLayers(boolean dispose) {
    if (!visibleLayers.isEmpty()) {
      visibleLayers.getFirst().deactivated();
      while (!visibleLayers.isEmpty()) {
        visibleLayers.removeFirst().hidden();
      }
    }
    constructedLayers.clear();
    for (Iterator<GdxLayer> it = layers.values().iterator(); it.hasNext(); ) {
      GdxLayer layer = it.next();
      it.remove();
      layer.setContainer(null);
      if (dispose) layer.dispose();
    }
  }

  private int getLayerId(GdxLayer layer) {
    for (Entry<Integer, GdxLayer> entry : layers.entrySet()) {
      if (entry.getValue() == layer) return entry.getKey();
    }
    throw new NoSuchElementException(String.format("Layer not found"));
  }
  
  public GdxLayer getActiveLayer() {
    if (visibleLayers.isEmpty()) return null;
    return visibleLayers.getFirst();
  }
  
  public void setActiveLayer(GdxLayer layer) {
    GdxLayer activeLayer = getActiveLayer();
    if (layer != activeLayer) {
      if (activeLayer != null) activeLayer.deactivated();
      if (!constructedLayers.remove(layer)) layer.construct();
      constructedLayers.addFirst(layer);
      layer.activated();
      visibleLayers.remove(layer);
      boolean obscured = isLayerObscuring(layer);
      for (Iterator<GdxLayer> it = visibleLayers.iterator(); it.hasNext(); ) {
        if (obscured) {
          it.next();
          it.remove();
        } else {
          obscured = isLayerObscuring(it.next());
        }
      }
      visibleLayers.addFirst(layer);
      disposeUnusedLayers(false);
      if (activeLayer != null) {
        for (GdxActiveLayerChangeListener listener : activeLayerChangeListeners) {
          listener.onActiveLayerChange(this, getLayerId(layer), getLayerId(activeLayer));
        }
      }
    }
  }
  
  public void setActiveLayer(int id) {
    GdxLayer layer = getLayer(id);
    if (layer != null) {
      setActiveLayer(layer);
    } else {
      throw new NoSuchElementException(String.format("Layer not found (id = %d)", id));
    }
  }
  
  private boolean isLayerObscuring(GdxLayer layer) {
    Rectangle area = layer.getArea();
    return !(layer.transparent()
        || area.x > 0 || area.y > 0 || area.x + area.width < getWidth() || area.y + area.height < getHeight());
  }
  
  private void disposeUnusedLayers(boolean forceDisposal) {
    if (constructedLayers.size() <= constructedLayerLimit) return;
    if (constructedLayers.size() <= visibleLayers.size()) return;
    for (Iterator<GdxLayer> it = constructedLayers.descendingIterator(); it.hasNext(); ) {
      GdxLayer layer = it.next();
      if (!forceDisposal && layer.neverDispose()) continue;
      if (visibleLayers.contains(layer)) continue;
      it.remove();
      layer.dispose();
      if (constructedLayers.size() <= constructedLayerLimit) break;
    }
  }

  public void addActiveLayerChangeListener(Object tag, GdxActiveLayerChangeListener listener) {
    activeLayerChangeListeners.add(tag, listener);
  }

  public boolean removeActiveLayerChangeListener(GdxActiveLayerChangeListener listener) {
    return activeLayerChangeListeners.remove(listener);
  }

  public GdxActiveLayerChangeListener removeActiveLayerChangeListener(Object tag) {
    return activeLayerChangeListeners.remove(tag);
  }

  @Override
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y) {
    GdxLayer activeLayer = getActiveLayer();
    if (activeLayer == null) {
      return DummyIterator.create((GdxComponent)activeLayer);
    } else {
      return SingleElementIterator.create((GdxComponent)activeLayer);
    }
  }
  
  @Override
  public void reportResize(GdxComponent component) { }
  
  @Override
  public void paint(float x, float y, GdxPainter painter) {
    for (Iterator<GdxLayer> it = visibleLayers.descendingIterator(); it.hasNext(); ) {
      GdxLayer layer = it.next();
      if (layer.isVisible()) {
        layer.paint(x + layer.getX(), y + layer.getY(), painter);
      }
    }
  }
  
  @Override
  public void dispose() {
    clearLayers(true);
  }
}
