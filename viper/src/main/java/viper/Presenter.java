package viper;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Feb-13, 22:33
 */
public abstract class Presenter<V extends View, R extends Router> {
  private V mView;
  private R mRouter;

  public void dropRouter() {
    mRouter = null;
  }

  protected void dropView() {
    mView = null;
  }

  protected final R getRouter() {
    return mRouter;
  }

  protected final V getView() {
    return mView;
  }

  public void takeRouter(final R router) {
    mRouter = router;
  }

  public void takeView(final V view) {
    mView = view;
  }
}
