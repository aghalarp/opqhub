package json;

import java.util.Set;

public class MapRequest extends JsonData {
  public Long startTimestamp;
  public Long stopTimestamp;
  public boolean requestFrequency;
  public boolean requestVoltage;
  public boolean requestHeartbeats;
  public Set<String> visibleIds;

  public static MapRequest fromJson(String json) {
    return JsonUtils.toObject(json, MapRequest.class);
  }
}
