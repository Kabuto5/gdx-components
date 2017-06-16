package io;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import components.GdxMainFrame;

public class GdxAssets implements Disposable {
  private HashMap<String, TextureRegion> textures = new HashMap<String, TextureRegion>();
  private HashMap<String, BitmapFont> fonts = new HashMap<String, BitmapFont>();
  private BitmapFont defaultFont = null;
  private TextureFilter defaultTextureFilter = TextureFilter.Linear;
  private static GdxMainFrame frame;

  /**
   * Creates an asset map for a given frame. Frame parameter is for backward compatibility only and will be removed in
   * future. Creating multiple instances of {@link GdxAssets} in its current state will lead to incorrect behaviour in
   * some cases.
   * @param frame Frame this instance is associated with
   */
  public GdxAssets(GdxMainFrame frame) {
    GdxAssets.frame = frame;
  }
  
  /**
   * @return The last instance created
   * @deprecated Accessing assets in a static way is deprecated. Use {@link components.GdxMainFrame#getAssets()} instead.
   */
  @Deprecated
  public static GdxAssets getAssets() {
    return frame.getAssets();
  }

  /**
   * @param group Asset grouping is no longer supported. This parameter is now meaningless.
   * @return The last instance created
   * @deprecated Accessing assets in a static way is deprecated. Use {@link components.GdxMainFrame#getAssets()} instead.
   */
  @Deprecated
  public static synchronized GdxAssets getAssets(String group) {
    return frame.getAssets();
  }
  
  public TextureFilter getDefaultTextureFilter() {
    return defaultTextureFilter;
  }

  public void setDefaultTextureFilter(TextureFilter textureFilter) {
    defaultTextureFilter = textureFilter;
  }

  public void addTexture(String name, Texture texture) {
    addTexture(name, texture, false);
  }

  public void addTexture(String name, Texture texture, boolean flipY) {
    addTexture(name, new TextureRegion(texture), flipY);
  }

  public void addTexture(String name, TextureRegion textureRegion) {
    addTexture(name, textureRegion, false);
  }
  
  public void addTexture(String name, TextureRegion textureRegion, boolean flipY) {
    if (name == null) throw new NullPointerException("Texture name cannot be null");
    if (textureRegion == null) throw new NullPointerException("No texture to be added");
    if (defaultTextureFilter != null)
      textureRegion.getTexture().setFilter(defaultTextureFilter, defaultTextureFilter);
    textureRegion.flip(false, !flipY); // Textures are flipped by default because of y-down coordinate system
    textures.put(name, textureRegion);
  }

  public boolean hasTexture(String name) {
    if (name == null) throw new NullPointerException("Texture name cannot be null");
    return textures.containsKey(name);
  }

  public TextureRegion getTexture(String name) {
    if (name == null) throw new NullPointerException("Texture name cannot be null");
    TextureRegion textureRegion = textures.get(name);
    if (textureRegion == null)
      throw new IllegalArgumentException("Texture not found: " + name);
    return textureRegion;
  }
  
  public void addFont(String name, BitmapFont font) {
    if (name == null) throw new NullPointerException("Font name cannot be null");
    if (font == null) throw new NullPointerException("No font to be added");
    if (defaultTextureFilter != null)
      for (TextureRegion textureRegion : font.getRegions()) {
        textureRegion.getTexture().setFilter(defaultTextureFilter, defaultTextureFilter);
      }
    if (fonts.size() == 0) defaultFont = font;
    fonts.put(name, font);
  }

  public boolean hasFont(String name) {
    if (name == null) throw new NullPointerException("Font name cannot be null");
    return fonts.containsKey(name);
  }

  public BitmapFont getFont(String name) {
    if (name == null) throw new NullPointerException("Font name cannot be null");
    BitmapFont font = fonts.get(name);
    if (font == null)
      throw new IllegalArgumentException("Font not found: " + name);
    return font;
  }

  public void setDefaultFont(String name) {
    if (name == null) throw new NullPointerException("Font name cannot be null");
    BitmapFont font = fonts.get(name);
    if (font == null)
      throw new IllegalArgumentException("Font not found: " + name);
    defaultFont = font;
  }

  public BitmapFont getDefaultFont() {
    if (defaultFont == null)
      defaultFont = new BitmapFont(true);
    return defaultFont;
  }
  
  public void dispose() {
    for (Iterator<TextureRegion> it = textures.values().iterator(); it.hasNext(); ) {
      it.next().getTexture().dispose();
      it.remove();
    }
    for (Iterator<BitmapFont> it = fonts.values().iterator(); it.hasNext(); ) {
      it.next().dispose();
      it.remove();
    }
    if (defaultFont != null) {
      defaultFont.dispose();
      defaultFont = null;
    }
    frame = null;
  }
  
  /**
   * @deprecated Managing assets in a static way is deprecated. Assets managed by {@link components.GdxMainFrame} are disposed automatically.
   */
  public static synchronized void disposeAll() { }
}
