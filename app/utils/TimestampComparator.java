package utils;

import java.util.Comparator;

public class TimestampComparator implements Comparator<Timestampable> {

  @Override
  public int compare(Timestampable o1, Timestampable o2) {
    return o2.getTimestamp().compareTo(o1.getTimestamp());
  }
}
