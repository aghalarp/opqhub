package json;

import java.util.ArrayList;
import java.util.List;

public class PublicEventResponse extends JsonData {
  public String timestamp;
  public String eventType;
  public String gridId;
  public Double centerLat;
  public Double centerLng;
  public Double gridScale;
  public Double frequency;
  public Double voltage;
  public String eventLevel;
  public Long duration;
  public List<Double> waveform;

  public PublicEventResponse() {
    super("public-event-response");
    this.timestamp = "";
    this.eventType = "";
    this.gridId = "";
    this.centerLat = 0.0;
    this.centerLng = 0.0;
    this.gridScale = 0.0;
    this.frequency = 0.0;
    this.voltage = 0.0;
    this.eventLevel = "";
    this.duration = 0L;
    this.waveform = new ArrayList<>();
  }
}
