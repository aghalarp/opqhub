package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class AlertNotification extends Model {
  @Id
  private Long primaryKey;

  // TODO: Figure out which fields need to be required
  private Boolean voltageAlertNotification;
  private Boolean frequencyAlertNotification;
  private Boolean alertViaEmail;
  private String notificationEmail;
  private Boolean alertViaSms;
  private String smsCarrier;
  private String smsNumber;
  private Double minAcceptableVoltage;
  private Double maxAcceptableVoltage;
  private Double minAcceptableFrequency;
  private Double maxAcceptableFrequency;

  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  // TODO: Add in constructor

  public static Finder<Long, AlertNotification> find() {
    return new Finder<>(Long.class, AlertNotification.class);
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Boolean getVoltageAlertNotification() {
    return voltageAlertNotification;
  }

  public void setVoltageAlertNotification(Boolean voltageAlertNotification) {
    this.voltageAlertNotification = voltageAlertNotification;
  }

  public Boolean getFrequencyAlertNotification() {
    return frequencyAlertNotification;
  }

  public void setFrequencyAlertNotification(Boolean frequencyAlertNotification) {
    this.frequencyAlertNotification = frequencyAlertNotification;
  }

  public Boolean getAlertViaEmail() {
    return alertViaEmail;
  }

  public void setAlertViaEmail(Boolean alertViaEmail) {
    this.alertViaEmail = alertViaEmail;
  }

  public String getNotificationEmail() {
    return notificationEmail;
  }

  public void setNotificationEmail(String notificationEmail) {
    this.notificationEmail = notificationEmail;
  }

  public Boolean getAlertViaSms() {
    return alertViaSms;
  }

  public void setAlertViaSms(Boolean alertViaSms) {
    this.alertViaSms = alertViaSms;
  }

  public String getSmsCarrier() {
    return smsCarrier;
  }

  public void setSmsCarrier(String smsCarrier) {
    this.smsCarrier = smsCarrier;
  }

  public String getSmsNumber() {
    return smsNumber;
  }

  public void setSmsNumber(String smsNumber) {
    this.smsNumber = smsNumber;
  }

  public Double getMinAcceptableVoltage() {
    return minAcceptableVoltage;
  }

  public void setMinAcceptableVoltage(Double minAcceptableVoltage) {
    this.minAcceptableVoltage = minAcceptableVoltage;
  }

  public Double getMaxAcceptableVoltage() {
    return maxAcceptableVoltage;
  }

  public void setMaxAcceptableVoltage(Double maxAcceptableVoltage) {
    this.maxAcceptableVoltage = maxAcceptableVoltage;
  }

  public Double getMinAcceptableFrequency() {
    return minAcceptableFrequency;
  }

  public void setMinAcceptableFrequency(Double minAcceptableFrequency) {
    this.minAcceptableFrequency = minAcceptableFrequency;
  }

  public Double getMaxAcceptableFrequency() {
    return maxAcceptableFrequency;
  }

  public void setMaxAcceptableFrequency(Double maxAcceptableFrequency) {
    this.maxAcceptableFrequency = maxAcceptableFrequency;
  }

  public OpqDevice getDevice() {
    return device;
  }

  public void setDevice(OpqDevice device) {
    this.device = device;
  }
}
