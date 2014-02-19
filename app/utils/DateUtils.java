package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utilities for switching between different units of time.
 */
public class DateUtils {
  /**
   * Number of miliseconds in certain time frames
   */
  public enum TimeUnit {
    Second  (1000L),
    Minute  (60000L),
    Hour    (36000000L),
    Day     (8640000000L),
    Week    (604800000000L),
    Month   (263000000000L),
    Year    (31560000000000L);

    final private Long milliseconds;

    TimeUnit(Long milliseconds) {
      this.milliseconds = milliseconds;
    }

    public Long getMilliseconds() {
      return this.milliseconds;
    }
  }

  public static Long getPastTime(Long timestamp, TimeUnit timeUnit) {
    return timestamp - timeUnit.getMilliseconds();
  }

  public static Long getMillis() {
    return new Date().getTime();
  }

  /**
   * Converts milliseconds since the epoch into a human readable format.
   * @param millisecondsSinceEpoch Milliseconds since the epoch.
   * @return Human formated string of timestamp.
   */
  public static final String toDateTime(Long millisecondsSinceEpoch) {
    Date dateStr = new Date(millisecondsSinceEpoch);

    return new SimpleDateFormat("HH:mm:ss.SSS [dd MMM YYYY]").format(dateStr);
  }
}
