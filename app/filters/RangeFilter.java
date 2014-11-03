package filters;

import com.avaje.ebean.Query;

public class RangeFilter<T, U> implements Filter<T> {
  private String field;
  private U min;
  private U max;


  public RangeFilter(String field, U min, U max) {
    this.field = field;
    this.min = min;
    this.max = max;
  }

  public static <T, U> RangeFilter<T, U> of(String field, U min, U max) {
    return new RangeFilter<>(field, min, max);
  }

  @Override
  public void apply(Query<T> query) {
    GreaterThanFilter.<T, U>of(field, min).apply(query);
    LessThanFilter.<T, U>of(field, max).apply(query);
  }
}
