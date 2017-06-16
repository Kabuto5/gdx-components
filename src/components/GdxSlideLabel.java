package components;

import io.GdxPainter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import components.abstracts.GdxAbstractComponent;

public class GdxSlideLabel extends GdxLabel {
  private float padding;
  private TextAlignment alignment;
  private VerticalAlignment verticalAlignment = VALIGN_MIDDLE;
  private Color color = null;
  private float scale = 1;
  private CharSequence text;
  private BitmapFont font;
  private String fontName;
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text, TextAlignment alignment, BitmapFont font) {
    super(x, y, width, height);
    this.padding = padding;
    this.alignment = alignment;
    this.text = text;
    this.font = font;
  }
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text, BitmapFont font) {
    this(x, y, width, height, padding, text, ALIGN_LEFT, font);
  }
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text, TextAlignment alignment, String fontName) {
    super(x, y, width, height);
    this.padding = padding;
    this.alignment = alignment;
    this.text = text;
    this.fontName = fontName;
  }
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text, String fontName) {
    this(x, y, width, height, padding, text, ALIGN_LEFT, fontName);
  }
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text, TextAlignment alignment) {
    this(x, y, width, height, padding, text, alignment, (String)null);
  }
  
  public GdxSlideLabel(float x, float y, float width, float height, float padding, CharSequence text) {
    this(x, y, width, height, padding, text, ALIGN_LEFT);
  }
  
  public float getPadding() {
    return padding;
  }

  public void setPadding(float padding) {
    this.padding = padding;
  }

  public TextAlignment getAlignment() {
    return alignment;
  }

  public void setAlignment(TextAlignment alignment) {
    this.alignment = alignment;
  }

  public VerticalAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }

  public void setText(CharSequence text) {
    this.text = text;
  }

  public void setFont(BitmapFont font) {
    this.font = font;
    this.fontName = null;
  }

  public void setFont(String fontName) {
    if (getFrame() == null) {
      this.font = null;
    } else {
      if (fontName == null) {
        this.font = getFrame().getAssets().getDefaultFont();
      } else {
        this.font = getFrame().getAssets().getFont(fontName);
      }
    }
    this.fontName = fontName;
  }

  public Color getColor() {
    return new Color(color);
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  public void setColor(float red, float green, float blue, float alpha) {
    color = new Color(red, green, blue, alpha);
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  @Override
  public void paint(float x, float y, GdxPainter painter) {
    if (text == null) return;
    if (font == null) {
      if (fontName == null) {
        font = getFrame().getAssets().getDefaultFont();
      } else {
        font = getFrame().getAssets().getFont(fontName);
      }
    }
    Color fontColor = font.getColor();
    if (color != null) font.setColor(color);
    float scaleX = font.getScaleX();
    float scaleY = font.getScaleY();
    font.getData().setScale(scaleX * scale, scaleY * scale);
    if (alignment == ALIGN_LEFT && verticalAlignment == VALIGN_TOP) {
      painter.drawText(font, text, x + padding, y + padding);
    }
    else {
      GlyphLayout bounds = new GlyphLayout(font, text);
      switch (alignment) {
      case CENTER: x += (getWidth() - bounds.width) * 0.5f; break;
      case RIGHT: x += getWidth() - bounds.width - padding; break;
      default: x += padding;
      }
      switch (verticalAlignment) {
      case MIDDLE: y += (getHeight() - bounds.height) * 0.5f; break;
      case BOTTOM: y += getHeight() - bounds.height - padding; break;
      default: y += padding;
      }
      painter.drawText(font, text, x, y);
    }
    font.getData().setScale(scaleX, scaleY);
    font.setColor(fontColor);
  }
}
