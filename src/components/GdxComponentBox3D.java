package components;

import java.util.Collection;
import java.util.Iterator;

import helpers.collections.DummyIterator;
import io.GdxPainter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import components.aggregated.GdxContent;
import components.interfaces.GdxComponent;
import components.interfaces.GdxContainer;

public class GdxComponentBox3D extends GdxBox3D implements GdxContainer {
  private final GdxContent content = new GdxContent(this);
  private TextureRegion currentTexture;
  
  public GdxComponentBox3D(GdxComponent component, Material material, 
      float x, float y, float z, float depth) {
    super(new Material(), material, x, y, z, component.getWidth(), component.getHeight(), depth);
    content.set(component);
  }

  @Override
  public void renderTexture(int id, float width, float height, GdxPainter painter) {
    content.get().paint(0, 0, painter);
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    TextureRegion componentTexture = painter.requestTextureRender(this, 0, getWidth(), getHeight(), Color.BLACK);
    if (currentTexture != componentTexture) {
      modelInstance.setMaterial(0, 0, TextureAttribute.createDiffuse(componentTexture));
      currentTexture = componentTexture;
    }
    super.paint(x, y, painter);
  }

  @Override
  public Collection<GdxComponent> getComponents() {
    return content.collection();
  }

  @Override
  public boolean hasComponent(GdxComponent component) {
    return content.is(component);
  }

  @Override
  public boolean removeComponent(GdxComponent component) {
    return content.remove(component);
  }

  @Override
  public Iterator<GdxComponent> interactionCandidatesIterator(float x, float y) {
    return DummyIterator.create(GdxComponent.class);
  }

  @Override
  public void reportResize(GdxComponent component) {
    setSize(component.getWidth(), component.getHeight());
  }

  @Override
  public boolean onTouchDown(float x, float y, int pointer) {
    return content.get().onTouchDown(x, y, pointer);
  }

  @Override
  public boolean onTouchUp(float x, float y, int pointer) {
    return content.get().onTouchUp(x, y, pointer);
  }

  @Override
  public boolean onDrag(float x, float y, float differenceX, float differenceY, int pointer) {
    return content.get().onDrag(x, y, differenceX, differenceY, pointer);
  }

  @Override
  public boolean onDragIn(float x, float y, float differenceX, float differenceY, int pointer) {
    return content.get().onDragIn(x, y, differenceX, differenceY, pointer);
  }

  @Override
  public boolean onDragOut(float x, float y, float differenceX, float differenceY, int pointer) {
    return content.get().onDragOut(x, y, differenceX, differenceY, pointer);
  }

  @Override
  public boolean onTap(float x, float y, int tapCount, int pointer) {
    return content.get().onTap(x, y, tapCount, pointer);
  }

  @Override
  public boolean onFling(float x, float y, float velocityX, float velocityY, int pointer) {
    return content.get().onFling(x, y, velocityX, velocityY, pointer);
  }

  @Override
  public boolean onDragReceived(float x, float y, float differenceX, float differenceY, int pointer) {
    return true;
  }

  @Override
  public void onDragCapturingStopped(float x, float y, int pointer) { }
}
