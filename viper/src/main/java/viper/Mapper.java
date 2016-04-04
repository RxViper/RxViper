package viper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmitriy Zaitsev
 * @since 2016-Apr-04, 02:21
 */
public abstract class Mapper<From, To> {
  public abstract To map(From entity);

  public final Collection<To> map(Collection<From> entities) {
    final Collection<To> result = new ArrayList<>(entities.size());
    //noinspection Convert2streamapi
    for (From from : entities) {
      result.add(map(from));
    }
    return result;
  }
}
