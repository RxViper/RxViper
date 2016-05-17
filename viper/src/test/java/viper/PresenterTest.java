package viper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-May-13, 12:31
 */
public class PresenterTest {
  @Mock ViewCallbacks                    mView;
  @Mock Router                           mRouter;
  @Spy  Presenter<ViewCallbacks, Router> mPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void shouldNotHaveView() {
    assertThat(mPresenter.getView()).isNull();
    assertThat(mPresenter.hasView()).isFalse();
  }

  @Test public void shouldNotHaveRouter() {
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldTakeView() {
    mPresenter.takeView(mView);
    assertThat(mPresenter.getView()).isEqualTo(mView);
    assertThat(mPresenter.hasView()).isTrue();
  }

  @Test public void shouldDropView() {
    mPresenter.takeView(mView);

    mPresenter.dropView();
    assertThat(mPresenter.getView()).isNull();
    assertThat(mPresenter.hasView()).isFalse();
  }

  @Test public void shouldTakeRouter() {
    mPresenter.takeRouter(mRouter);
    assertThat(mPresenter.getRouter()).isEqualTo(mRouter);
    assertThat(mPresenter.hasRouter()).isTrue();
  }

  @Test public void shouldDropRouter() {
    mPresenter.takeRouter(mRouter);

    mPresenter.dropRouter();
    assertThat(mPresenter.getRouter()).isNull();
    assertThat(mPresenter.hasRouter()).isFalse();
  }

  @Test public void shouldCallOnTakeView() {
    mPresenter.takeView(mView);
    verify(mPresenter).onTakeView(mView);
  }

  @Test public void shouldCallOnTakeRouter() {
    mPresenter.takeRouter(mRouter);
    verify(mPresenter).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnDropView() {
    mPresenter.dropView();
    verify(mPresenter).onDropView();
  }

  @Test public void shouldCallOnDropRouter() {
    mPresenter.dropRouter();
    verify(mPresenter).onDropRouter();
  }

  @Test public void shouldCallOnTakeViewAfterViewIsTaken() {
    mPresenter.takeView(mView);
    mPresenter.dropView();

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).assignView(mView);
    (inOrder.verify(mPresenter)).onTakeView(mView);
  }

  @Test public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter();

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).assignRouter(mRouter);
    (inOrder.verify(mPresenter)).onTakeRouter(mRouter);
  }

  @Test public void shouldCallOnDropViewBeforeViewIsDropped() {
    mPresenter.takeView(mView);
    mPresenter.dropView();

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).onDropView();
    (inOrder.verify(mPresenter)).releaseView();
  }

  @Test public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter();

    final InOrder inOrder = Mockito.inOrder(mPresenter);
    (inOrder.verify(mPresenter)).onDropRouter();
    (inOrder.verify(mPresenter)).releaseRouter();
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenViewShouldNotBeNull() {
    mPresenter.takeView(null);
  }

  @Test(expected = IllegalArgumentException.class) //
  public void takenRouterShouldNotBeNull() {
    mPresenter.takeRouter(null);
  }
}