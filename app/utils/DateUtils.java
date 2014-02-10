package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utilities for switching between different units of time.
 */
public class DateUtils {
  /**
   * Converts milliseconds since the epoch into a human readable format.
   * @param millisecondsSinceEpoch Milliseconds since the epoch.
   * @return Human formated string of timestamp.
   */
  public static final String toDateTime(Long millisecondsSinceEpoch) {
    Date dateStr = new Date(millisecondsSinceEpoch);

    return new SimpleDateFormat("MM-dd-YY HH:mm:ss.S z").format(dateStr);
  }
}
