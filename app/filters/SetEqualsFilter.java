package filters;

import java.util.Set;

import com.avaje.ebean.Query;

public class SetEqualsFilter<T, U> implements Filter<T> {
  private String field;
  private Set<U> match;

  public SetEqualsFilter(String field, Set<U> match) {
    this.field = field;
    this.match = match;
  }

  public static <T, U> SetEqualsFilter<T, U> of(String field, Set<U> match) {
    return new SetEqualsFilter<>(field, match);
  }

  @Override
  public void apply(Query<T> query) {
    query.where().in(field, match);
  }
}
