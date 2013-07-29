package models;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Measurement extends Model {
  @Id
  private Long primaryKey;

  @Required
  private Long timestamp;

  @Required
  private Double voltage;

  @Required
  private Double frequency;

  @Required
  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  public Measurement(Long timestamp, Double voltage, Double frequency) {
    this.setTimestamp(timestamp);
    this.setVoltage(voltage);
    this.setFrequency(frequency);
  }

  public static Finder<Long, Measurement> find() {
    return new Finder<>(Long.class, Measurement.class);
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Double getVoltage() {
    return voltage;
  }

  public void setVoltage(Double voltage) {
    this.voltage = voltage;
  }

  public Double getFrequency() {
    return frequency;
  }

  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  public OpqDevice getDevice() {
    return this.device;
  }

  public void setDevice(OpqDevice device) {
    this.device = device;
  }
}
