package models;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Alert extends Model {


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

  @Id
  private Long primaryKey;

  @Required
  private AlertType alertType;

  @Required
  private Double alertValue;

  @Required
  private Long timestamp;

  @Required
  private Long alertDuration;

  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  @ManyToOne(cascade = CascadeType.ALL)
  private ExternalEvent externalEvent;

  public Alert(AlertType alertType, Double alertValue, Long timestamp, Long alertDuration) {
    this.setAlertType(alertType);
    this.setAlertValue(alertValue);
    this.setTimestamp(timestamp);
    this.setAlertDuration(alertDuration);
  }

  public static Finder<Long, Alert> find() {
    return new Finder<>(Long.class, Alert.class);
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public AlertType getAlertType() {
    return alertType;
  }

  public void setAlertType(AlertType alertType) {
    this.alertType = alertType;
  }

  public Double getAlertValue() {
    return alertValue;
  }

  public void setAlertValue(Double alertValue) {
    this.alertValue = alertValue;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getAlertDuration() {
    return alertDuration;
  }

  public void setAlertDuration(Long alertDuration) {
    this.alertDuration = alertDuration;
  }

  public OpqDevice getDevice() {
    return device;
  }

  public void setDevice(OpqDevice device) {
    this.device = device;
  }

  public ExternalEvent getExternalEvent() {
    return externalEvent;
  }

  public void setExternalEvent(ExternalEvent externalEvent) {
    this.externalEvent = externalEvent;
  }

}
