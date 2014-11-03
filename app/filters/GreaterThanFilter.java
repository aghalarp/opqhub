package filters;

import com.avaje.ebean.Query;

public class GreaterThanFilter<T, U> implements Filter<T> {
  private String field;
  private U value;

  public GreaterThanFilter(String field, U value) {
    this.field = field;
    this.value = value;
  }

  public static <T, U> LessThanFilter<T, U> of(String field, U value) {
    return new LessThanFilter<>(field, value);
  }

  @Override
  public void apply(Query<T> query) {
    query.where().gt(field, value);
  }
}
