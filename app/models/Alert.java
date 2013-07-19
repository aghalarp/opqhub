package models;

import java.util.Date;

public class Alert {

  public enum AlertType {
    DEVICE("Device"),
    FREQUENCY("Frequency"),
    VOLTAGE("Voltage");

    private String name;

    private AlertType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private AlertType alertType;
  private Date date;
  private Long duration;
  private Double alertValue;
  private String description;

  // These will eventually be removed in favor of a key to the OpqDevice in the DB. These are serving mockup purposes
  // only.
  public Double latitude;
  public Double longitude;

  // These will eventually be removed in favor of a key to the OpqDevice in the DB. These are serving mockup purposes
  // only.
  public Alert (AlertType alertType, Date date, Long duration, Double alertValue, String description, Double latitude, Double longitude) {
    this.alertType = alertType;
    this.date = date;
    this.duration = duration;
    this.alertValue = alertValue;
    this.description = description;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Alert (AlertType alertType, Date date, Long duration, Double alertValue, String description) {
    this.alertType = alertType;
    this.date = date;
    this.duration = duration;
    this.alertValue = alertValue;
    this.description = description;
  }

  public AlertType getAlertType() {
    return alertType;
  }

  public Date getDate() {
    return date;
  }

  public Long getDuration() {
    return duration;
  }

  public Double getAlertValue() {
    return alertValue;
  }

  public String getDescription() {
    return description;
  }
}
