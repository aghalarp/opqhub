/*
  This file is part of opq-ao.

  opa-ao is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opa-ao is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opq-ao.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2013 Anthony Christe
 */

package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Contains methods for viewing managing persistent AlertNotifications.
 * <p/>
 * AlertNotifications are created by the user to determine what their OPQ device views as power quality alerts. They can
 * also be configured so that SMS messages or emails are generated when an alert is triggered.
 */
@Entity
public class AlertNotification extends Model {
  /**
   * Primary key.
   */
  @Id
  private Long primaryKey;

  // TODO: Figure out which fields need to be required
  /**
   * If true, then voltage should be monitored using min/max values below.
   */
  private Boolean voltageAlertNotification;

  /**
   * If true, then frequency should be monitored using min/max values below.
   */
  private Boolean frequencyAlertNotification;

  /**
   * If true, then alerts will be triggered on device malfunctions
   */
  private Boolean deviceAlertNotification;

  // TODO: Implement sending of e-mail on alert.
  /**
   * If true, alerts should trigger an e-mail message.
   */
  private Boolean alertViaEmail;

  /**
   * E-mail address that alert e-mails should be sent to.
   */
  private String notificationEmail;

  // TODO: Implement sending of SMS on alert.
  /**
   * If true, alerts should trigger an SMS message.
   */
  private Boolean alertViaSms;

  /**
   * Carrier that user wants alerts sent to. This currently only works for a couple of major carriers in the United
   * States.
   */
  private String smsCarrier;

  /**
   * Number of user's SMS device.
   */
  private String smsNumber;

  /**
   * The minimum voltages (in Volts) that should not trigger alert. Any value less than this minimum should trigger an
   * alert.
   */
  private Double minAcceptableVoltage;

  /**
   * Maximum voltage (in Volts) that should not trigger alert. Any value greater than this maximum should trigger an
   * alert.
   */
  private Double maxAcceptableVoltage;

  /**
   * The minimum frequency (in Hertz) that should not trigger an alert. Any value less than this minimum should trigger
   * an alert.
   */
  private Double minAcceptableFrequency;

  /**
   * The maximum frequency (in Hertz) that should not trigger an alert. Any value greate than this maximum should
   * trigger an alert.
   */
  private Double maxAcceptableFrequency;

  /**
   * Convenience method for test package.
   *
   * @param voltageAlertNotification   Should bad voltages trigger an alert.
   * @param frequencyAlertNotification Should bad frequencies trigger an alert.
   * @param alertViaEmail              Should users be e-mailed on alerts.
   * @param alertViaSms                Should users be texted on alerts.
   * @param smsCarrier                 Carrier of users text service.
   * @param smsNumber                  Users sms number.
   * @param notificationEmail          Users notification email.
   * @param minAcceptableFrequency     Min frequency that will not trigger alert.
   * @param maxAcceptableFrequency     Max frequency that will not trigger alert.
   * @param minAcceptableVoltage       Min voltage that will not trigger an alert.
   * @param maxAcceptableVoltage       Max voltage that will not trigger an alert.
   */
  public AlertNotification(Boolean voltageAlertNotification, Boolean frequencyAlertNotification,
                           Boolean alertViaEmail, Boolean alertViaSms, String smsCarrier, String smsNumber,
                           String notificationEmail, Double minAcceptableFrequency, Double maxAcceptableFrequency,
                           Double minAcceptableVoltage, Double maxAcceptableVoltage) {
    this.setVoltageAlertNotification(voltageAlertNotification);
    this.setFrequencyAlertNotification(frequencyAlertNotification);
    this.setAlertViaEmail(alertViaEmail);
    this.setAlertViaSms(alertViaSms);
    this.setSmsCarrier(smsCarrier);
    this.setSmsNumber(smsNumber);
    this.setNotificationEmail(notificationEmail);
    this.setMinAcceptableFrequency(minAcceptableFrequency);
    this.setMaxAcceptableFrequency(maxAcceptableFrequency);
    this.setMinAcceptableVoltage(minAcceptableVoltage);
    this.setMaxAcceptableVoltage(maxAcceptableVoltage);
  }

  /**
   * Many alert notifications may be set up for a single device.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  // TODO: Add in constructor

  /**
   * Create a finder for finding persisted AlertNotifications.
   *
   * @return A new finder for finding persisted AlertNotifications.
   */
  public static Finder<Long, AlertNotification> find() {
    return new Finder<>(Long.class, AlertNotification.class);
  }

  /**
   * Get the primary key.
   *
   * @return The primary key.
   */
  public Long getPrimaryKey() {
    return primaryKey;
  }

  /**
   * Set the primary key.
   *
   * @param primaryKey The primary key.
   */
  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Tests whether or not alerts should be triggered on device malfunctions.
   *
   * @return Whether or not alerts should be triggered on device malfunctions.
   */
  public Boolean getDeviceAlertNotification() {
    return this.deviceAlertNotification;
  }

  /**
   * Set whether or not alerts should be triggered on device malfunctions.
   *
   * @param deviceAlertNotification True to trigger, false otherwise.
   */
  public void setDeviceAlertNotification(Boolean deviceAlertNotification) {
    this.deviceAlertNotification = deviceAlertNotification;
  }

  /**
   * Tests whether or not alerts should be triggered for abnormal voltages.
   *
   * @return Should voltage alerts be triggered.
   */
  public Boolean getVoltageAlertNotification() {
    return voltageAlertNotification;
  }

  /**
   * Set whether or not alerts should be triggered for abnormal voltage readings.
   *
   * @param voltageAlertNotification True to trigger, false otherwise.
   */
  public void setVoltageAlertNotification(Boolean voltageAlertNotification) {
    this.voltageAlertNotification = voltageAlertNotification;
  }

  /**
   * Tests whether or not alerts should be triggered for abnormal frequency readings.
   *
   * @return Should frequency alerts be triggered.
   */
  public Boolean getFrequencyAlertNotification() {
    return frequencyAlertNotification;
  }

  /**
   * Set whether or not alerts should be triggered for abnormal frequency readings.
   *
   * @param frequencyAlertNotification True to trigger, false otherwise.
   */
  public void setFrequencyAlertNotification(Boolean frequencyAlertNotification) {
    this.frequencyAlertNotification = frequencyAlertNotification;
  }

  /**
   * Tests whether alerts should trigger an e-mail notification.
   *
   * @return Whether alerts should trigger an e-mail notification.
   */
  public Boolean getAlertViaEmail() {
    return alertViaEmail;
  }

  /**
   * Set whether or not users should be notified by e-mail on alerts.
   *
   * @param alertViaEmail If true alerts will trigger e-mails, false otherwise.
   */
  public void setAlertViaEmail(Boolean alertViaEmail) {
    this.alertViaEmail = alertViaEmail;
  }

  /**
   * Get e-mail address for e-mail notifications.
   *
   * @return The e-mail address associated with this alert notification.
   */
  public String getNotificationEmail() {
    return notificationEmail;
  }

  /**
   * Sets the e-mail address associated with this alert notification.
   *
   * @param notificationEmail E-mail address associated with this alert notification.
   */
  public void setNotificationEmail(String notificationEmail) {
    this.notificationEmail = notificationEmail;
  }

  /**
   * Tests whether or not this alert notification should trigger an sms notification.
   *
   * @return if true, then sms notifications will be sent, false otherwise.
   */
  public Boolean getAlertViaSms() {
    return alertViaSms;
  }

  /**
   * Sets whether or not this alert notification should trigger an sms notification.
   *
   * @param alertViaSms True to trigger an sms notification, false otherwise.
   */
  public void setAlertViaSms(Boolean alertViaSms) {
    this.alertViaSms = alertViaSms;
  }

  /**
   * Gets the sms carrier name associated with this alert notification.
   *
   * @return The sms carrier name associated with this alert notification.
   */
  public String getSmsCarrier() {
    return smsCarrier;
  }

  /**
   * Sets the sms carrier associated with this alert notification.
   *
   * @param smsCarrier The name of the SMS carrier associated with this notification. See utils/FormUtils.java for an a
   *                   list of currently available carriers.
   */
  public void setSmsCarrier(String smsCarrier) {
    this.smsCarrier = smsCarrier;
  }

  /**
   * Gets the sms number associated with this alert notification.
   *
   * @return The sms number associated with this alert notification.
   */
  public String getSmsNumber() {
    return smsNumber;
  }

  /**
   * Sets the sms number associated with this alert notification.
   *
   * @param smsNumber The sms number associated with this alert notification. The format of the number should be
   *                  ########## as a string.
   */
  public void setSmsNumber(String smsNumber) {
    this.smsNumber = smsNumber;
  }

  /**
   * Gets the minimum voltage that will not trigger an alert.
   *
   * @return The minimum voltage in volts.
   */
  public Double getMinAcceptableVoltage() {
    return minAcceptableVoltage;
  }

  /**
   * Sets the minimum voltage that will not cause an alert.
   *
   * @param minAcceptableVoltage Minimum voltage (in volts).
   */
  public void setMinAcceptableVoltage(Double minAcceptableVoltage) {
    this.minAcceptableVoltage = minAcceptableVoltage;
  }

  /**
   * Gets maximum voltage that will not trigger an alert.
   *
   * @return Maximum voltage (in volts).
   */
  public Double getMaxAcceptableVoltage() {
    return maxAcceptableVoltage;
  }

  /**
   * Sets maximum voltage that will not cause an alert.
   *
   * @param maxAcceptableVoltage Maximum voltage (in volts).
   */
  public void setMaxAcceptableVoltage(Double maxAcceptableVoltage) {
    this.maxAcceptableVoltage = maxAcceptableVoltage;
  }

  /**
   * Gets minimum frequency that will not cause an alert.
   *
   * @return Minimum frequency (in Hertz).
   */
  public Double getMinAcceptableFrequency() {
    return minAcceptableFrequency;
  }

  /**
   * Sets minimum frequency that should not cause an alert.
   *
   * @param minAcceptableFrequency Minimum frequency (in Hertz).
   */
  public void setMinAcceptableFrequency(Double minAcceptableFrequency) {
    this.minAcceptableFrequency = minAcceptableFrequency;
  }

  /**
   * Gets maximum frequency that will not cause an alert.
   *
   * @return Maximum frequency (in Hertz).
   */
  public Double getMaxAcceptableFrequency() {
    return maxAcceptableFrequency;
  }

  /**
   * Sets the maximum frequency that should not cause an alert.
   *
   * @param maxAcceptableFrequency Maximum frequency (in Hertz).
   */
  public void setMaxAcceptableFrequency(Double maxAcceptableFrequency) {
    this.maxAcceptableFrequency = maxAcceptableFrequency;
  }

  /**
   * Gets device associated with this alert notification.
   *
   * @return Device associated with this alert notification.
   */
  public OpqDevice getDevice() {
    return device;
  }

  /**
   * Sets the device associated with this alert notification.
   *
   * @param device Device associated with this alert notification.
   */
  public void setDevice(OpqDevice device) {
    this.device = device;
  }
}
