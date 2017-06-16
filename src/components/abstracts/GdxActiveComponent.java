package components.abstracts;

/**
 * Please note that functionality of this class has been incorporated into {@link GdxAbstractComponent}
 * in much more flexible way and this class shouldn't be extended anymore.
 * @deprecated Use {@link GdxAbstractComponent#makeActive()} in your constructor instead.
 *
 */
@Deprecated
public abstract class GdxActiveComponent extends GdxAbstractComponent {
  public GdxActiveComponent(float x, float y, float width, float height) {
    super(x, y, width, height);
    makeActive();
  }
}
