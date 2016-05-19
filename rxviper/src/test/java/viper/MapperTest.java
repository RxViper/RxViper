package viper;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-May-14, 13:32
 */
public class MapperTest {

  private Mapper<Integer, String> mMapper;

  @Before public void setUp() {
    mMapper = spy(new Mapper<Integer, String>() {
      @Override public String map(final Integer entity) {
        return String.valueOf(entity);
      }
    });
  }

  @Test public void shouldCallOverloadedMethod() {
    mMapper.map(asList(1, 2, 3));

    verify(mMapper).map(1);
    verify(mMapper).map(2);
    verify(mMapper).map(3);
  }
}