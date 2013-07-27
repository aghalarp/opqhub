package models;

public class OpqDevice {
  private Long deviceId;
  private Double longitude;
  private Double latitude;

  public OpqDevice(Long deviceId, Double latitude, Double longitude) {
    this.deviceId = deviceId;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Long getDeviceId() {
    return deviceId;
  }

  public Double getLongitude() {
    return longitude;
  }

  public Double getLatitude() {
    return latitude;
  }
}
