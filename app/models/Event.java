/*
  This file is part of OPQHub.

  OPQHub is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  OPQHub is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with OPQHub.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2014 Anthony Christe
 */

package models;

import org.openpowerquality.protocol.OpqPacket;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * This contains methods for viewing and modifying the persistent Event object.
 * <p/>
 * Alerts are triggered when power quality is not up to snuff. They indicate that either the voltage or frequency are
 * off the status-quo, or that an error has occurred with the user's opq-device.
 */
@Entity
public class Event extends Model implements Comparable<Event> {
  /* ----- Fields ----- */
  @Id
  private Long primaryKey;

  @Required
  private Long timestamp;

  @Required
  private OpqPacket.PacketType eventType;

  @Required
  private Double frequency;

  @Required
  private Double voltage;

  /*
  /**
   * Contains raw power data.
   *
  @Column(columnDefinition = "MEDIUMTEXT")
  private String rawPowerData;
  */


  @Required
  private Long duration;


  /* ----- Relationships ----- */
  @ManyToOne(cascade = CascadeType.ALL)
  private AccessKey accessKey;
  @ManyToOne(cascade = CascadeType.ALL)
  private Location location;
  @OneToOne
  private EventData eventData;

  /**
   * Convenience method for creating an event during testing.
   *
   * @param eventType     The event type.
   * @param eventValue    The event value (either in Hertz or Volts depending on the type).
   * @param timestamp     Timestamp for when alert happened representing number of milliseconds since epoch.
   * @param eventDuration Number of milliseconds that event occurred for.
   */
  public Event(Long timestamp, OpqPacket.PacketType eventType, Double frequency, Double voltage, Long duration) {
    this.setTimestamp(timestamp);
    this.setEventType(eventType);
    this.setFrequency(frequency);
    this.setVoltage(voltage);
    this.setDuration(duration);
  }

  /**
   * Create a new finder for persisted alerts.
   *
   * @return A new finder for persisted alerts.
   */
  public static Finder<Long, Event> find() {
    return new Finder<>(Long.class, Event.class);
  }


  /**
   * Primary Key.
   */
  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Time alert occurred as milliseconds since the epoch.
   */
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * The type of alert as given by the AlertType Enum.
   */
  public OpqPacket.PacketType getEventType() {
    return eventType;
  }

  public void setEventType(OpqPacket.PacketType eventType) {
    this.eventType = eventType;
  }

  /**
   * The frequency of the event.
   */
  public Double getFrequency() {
    return frequency;
  }

  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  /**
   * The voltage of the event.
   */
  public Double getVoltage() {
    return voltage;
  }

  public void setVoltage(Double voltage) {
    this.voltage = voltage;
  }

  /**
   * The amount of time the alert lasted in milliseconds.
   */
  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Many alerts can be associated with a single device.
   */
  public AccessKey getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(AccessKey accessKey) {
    this.accessKey = accessKey;
  }

  /**
   * Many alerts can be associated with an external event.
   */
  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public EventData getEventData() {
    return eventData;
  }

  public void setEventData(EventData eventData) {
    this.eventData = eventData;
  }

  @Override
  public int compareTo(Event event) {    
    return event.timestamp.compareTo(this.timestamp);
  }
}
