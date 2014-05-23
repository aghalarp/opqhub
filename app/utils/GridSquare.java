package utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class used for mapping a gridId -> set a metrics about that grid square.
 */
public class GridSquare {
  public class IticPoint {
    public final double voltageValue;
    public final long duration;

    public IticPoint(final long duration, final double voltageValue) {
      this.voltageValue = voltageValue;
      this.duration = duration;
    }

    @Override
    public String toString() {
      return "[" + this.duration + "," + this.voltageValue + "]";
    }
  }

  public String gridId;
  public int numDevices = 0;
  public int numAffectedDevices = 0;
  public int numFrequencyEvents = 0;
  public int numVoltageEvents = 0;
  public List<IticPoint> iticPoints = new LinkedList<>();

  public void addIticPoint(long duration, double voltage) {
    iticPoints.add(new IticPoint(duration, voltage));
  }
}
