package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utilities for switching between different units of time.
 */
public class DateUtils {
  /**
   * Number of milliseconds in certain time frames
   */
  public enum TimeUnit {
    Minute  (60000L),
    Hour    (3600000L),
    Day     (86400000L),
    Week    (604800000L),
    Month   (2592000000L),
    Year    (31536000000L);

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
   * @return Human formatted string of timestamp.
   */
  public static String toDateTime(Long millisecondsSinceEpoch) {
    Date dateStr = new Date(millisecondsSinceEpoch);

    return new SimpleDateFormat("HH:mm:ss.SSS [dd MMM YYYY]").format(dateStr);
  }
}
