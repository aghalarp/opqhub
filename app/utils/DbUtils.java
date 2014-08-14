package utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DbUtils {
  public static <T> Query<T> getAnyLike(Class<T> clazz, String field, Set<String> values) {
    return getAny(clazz, field, values, "%s like ?");
  }

  public static <T> Query<T> getAnyStartingWith(Class<T> clazz, String field, Set<String> values) {
    return getAny(clazz, field, values, "%s startsWith ?");
  }

  public static <T> Query<T> getAny(Class<T> clazz, String field, Set<String> values, String queryStr) {
    List<String> sqlList = new ArrayList<>();
    List<Object> paramsList = new ArrayList<>();
    Query<T> query = Ebean.createQuery(clazz);
    String sql = String.format(queryStr, field);

    for (String value : values) {
      sqlList.add(sql);
      paramsList.add(String.format("%s%%", value));
    }

    query.where(StringUtils.join(sqlList, " OR "));
    //Logger.info(or(sqlList).toString());

    int i = 1;
    for (Object param : paramsList) {
      query.setParameter(i++, param);
    }
    //Logger.info(query.);
    return query;
  }

  public static String or(List<String> ors) {
    return StringUtils.join(ors, " OR ");
  }

  public static String and(List<String> ands) {
    return StringUtils.join(ands, " AND ");
  }

  public static <T> Query<T> matchAny(Class clazz, String anyMatch, String query, Set<String> anyMatchSet, Set<String> otherQueries) {
    List<String> queryGroup = new ArrayList<String>(otherQueries);
    List<String> queryGroups = new ArrayList<String>();
    String queryStr;

    for(String q : anyMatchSet) {
      queryGroup = new ArrayList<String>(otherQueries);
      queryGroup.add(0, String.format("%s %s %s%%", anyMatch, query, q));
      queryGroups.add(String.format("(%s)", and(queryGroup)));
    }

    queryStr = or(queryGroups);
    return Ebean.createQuery(clazz, queryStr);
  }
}
