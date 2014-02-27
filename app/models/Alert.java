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

import org.openpowerquality.protocol.OpqPacket;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * This contains methods for viewing and modifying the persistent Alert object.
 * <p/>
 * Alerts are triggered when power quality is not up to snuff. They indicate that either the voltage or frequency are
 * off the status-quo, or that an error has occurred with the user's opq-device.
 */
@Entity
public class Alert extends Model {
  /**
   * Primary Key.
   */
  @Id
  private Long primaryKey;
  /**
   * The type of alert as given by the AlertType Enum.
   */
  @Required
  private OpqPacket.PacketType eventType;
  /**
   * The value of the alert in either Hertz or Volts depending on the alert type.
   */
  @Required
  private Double eventValue;
  /**
   * Time alert occurred as milliseconds since the epoch.
   */
  @Required
  private Long timestamp;
  /**
   * The amount of time the alert lasted in milliseconds.
   */
  @Required
  private Long eventDuration;
  /**
   * Many alerts can be associated with a single device.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;
  /**
   * Many alerts can be associated with an external event.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private ExternalEvent externalEvent;

  /**
   * Convenience method for creating an event during testing.
   *
   * @param eventType     The event type.
   * @param eventValue    The event value (either in Hertz or Volts depending on the type).
   * @param timestamp     Timestamp for when alert happened representing number of milliseconds since epoch.
   * @param eventDuration Number of milliseconds that evebt occurred for.
   */
  public Alert(OpqDevice device, OpqPacket.PacketType eventType, Long timestamp, Long eventDuration, Double eventValue) {
    this.setDevice(device);
    this.setEventType(eventType);
    this.setEventValue(eventValue);
    this.setTimestamp(timestamp);
    this.setEventDuration(eventDuration);
  }

  /**
   * Create a new finder for persisted alerts.
   *
   * @return A new finder for persisted alerts.
   */
  public static Finder<Long, Alert> find() {
    return new Finder<>(Long.class, Alert.class);
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
   * Get alert type.
   *
   * @return The alert type.
   */
  public OpqPacket.PacketType getEventType() {
    return eventType;
  }

  /**
   * Set alert type.
   * <p/>
   * The alert type should be a member of the AlertType Enum.
   *
   * @param eventType The alert type.
   */
  public void setEventType(OpqPacket.PacketType eventType) {
    this.eventType = eventType;
  }

  /**
   * Get the alert value in hertz or volts depending on the alert type.
   *
   * @return The alert value in hertz or volts.
   */
  public Double getEventValue() {
    return eventValue;
  }

  /**
   * Set the alert value in either hertz or volts depending on the alert type.
   *
   * @param eventValue Value of the alert in either hertz or volts depending on the alert type.
   */
  public void setEventValue(Double eventValue) {
    this.eventValue = eventValue;
  }

  /**
   * Get the timestamp of an alert represented as the number of milliseconds since the epoch.
   *
   * @return The time an alert occured in milliseconds since the epoch.
   */
  public Long getTimestamp() {
    return timestamp;
  }

  /**
   * Set the timestamp of an alert.
   *
   * @param timestamp Timestamp of an alert represented as number of milliseconds since the epoch.
   */
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Get the duration of an alert.
   *
   * @return The duration of an alert represented in milliseconds.
   */
  public Long getEventDuration() {
    return eventDuration;
  }

  /**
   * Set the duration of an alert.
   *
   * @param eventDuration Duration of an alert represented in milliseconds.
   */
  public void setEventDuration(Long eventDuration) {
    this.eventDuration = eventDuration;
  }

  /**
   * Get the device associated with this alert.
   *
   * @return Device associated with this alert.
   */
  public OpqDevice getDevice() {
    return device;
  }

  /**
   * Set the device associated with this alert.
   *
   * @param device The device associated with this alert.
   */
  public void setDevice(OpqDevice device) {
    this.device = device;
  }

  /**
   * Get the external event associated with this alert (if one exists).
   *
   * @return External event associated with this alert.
   */
  public ExternalEvent getExternalEvent() {
    return externalEvent;
  }

  /**
   * Set the external event associated with this alert.
   *
   * @param externalEvent External event associated with this alert.
   */
  public void setExternalEvent(ExternalEvent externalEvent) {
    this.externalEvent = externalEvent;
  }

}
