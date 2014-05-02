package utils;

/**
 * Utility class used for mapping a gridId -> set a metrics about that grid square.
 */
public class GridSquare {
  public String gridId;
  public int numDevices = 0;
  public int numAffectedDevices = 0;
  public int numFrequencyEvents = 0;
  public int numVoltageEvents = 0;
}
