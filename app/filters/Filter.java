package filters;

import com.avaje.ebean.Query;

/**
 * Created by anthony on 10/29/14.
 */
public interface Filter<T> {
  public void apply(Query<T> query);
}
