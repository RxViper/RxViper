package viper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-May-13, 12:31
 */
public class PresenterTest {
  @Mock   ViewCallbacks                    mView;
  @Mock   Router                           mRouter;
  private Presenter<ViewCallbacks, Router> mPresenter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    mPresenter = spy(new Presenter<ViewCallbacks, Router>() {
    });
  }

  @Test public void shouldNotHaveView() {
    assertThat(mPresenter.getView()).isNull();
  }

  @Test public void shouldNotHaveRouter() {
    assertThat(mPresenter.getRouter()).isNull();
  }

  @Test public void shouldTakeView() {
    mPresenter.takeView(mView);
    assertThat(mPresenter.getView()).isEqualTo(mView);
  }

  @Test public void shouldTakeRouter() {
    mPresenter.takeRouter(mRouter);
    assertThat(mPresenter.getRouter()).isEqualTo(mRouter);
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
    mPresenter = new Presenter<ViewCallbacks, Router>() {
      @Override protected void onTakeView(final ViewCallbacks view) {
        assertThat(getView()).isNotNull();
      }
    };
    mPresenter.takeView(mView);
    mPresenter.dropView();
  }

  @Test public void shouldCallOnTakeRouterAfterRouterIsTaken() {
    mPresenter = new Presenter<ViewCallbacks, Router>() {
      @Override protected void onTakeRouter(final Router router) {
        assertThat(getRouter()).isNotNull();
      }
    };
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter();
  }

  @Test public void shouldCallOnDropViewBeforeViewIsDropped() {
    mPresenter = new Presenter<ViewCallbacks, Router>() {
      @Override protected void onDropView() {
        assertThat(getView()).isNotNull();
      }
    };
    mPresenter.takeView(mView);
    mPresenter.dropView();
  }

  @Test public void shouldCallOnDropRouterBeforeRouterIsDropped() {
    mPresenter = new Presenter<ViewCallbacks, Router>() {
      @Override protected void onDropRouter() {
        assertThat(getRouter()).isNotNull();
      }
    };
    mPresenter.takeRouter(mRouter);
    mPresenter.dropRouter();
  }
}