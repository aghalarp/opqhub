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

// TODO: Make UI for managing external events.

package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * This contains methods relating to the persistent external event object.
 *
 * External events can be created by users in order to "tag" power events as being caused by some outside event.
 * Maybe a storm came through, a tree hit the power lines, or somebody ran into a telephone pole.
 */
@Entity
public class ExternalEvent extends Model {
  /**
   * The primary key.
   */
  @Id
  private Long primaryKey;

  // TODO: Should this be contrained to an enum?
  /**
   * The type of event that caused a power alert.
   * I.e. weather, man-made, etc...
   */
  @Constraints.Required
  private String eventType;

  /**
   * A more detailed description of the external event.
   *
   * I.e., if the event type was weather, the description might be "Tropical Storm Flossie".
   */
  @Constraints.Required
  private String eventDescription;

  /**
   * Timestamp of external event as milliseconds since the epoch.
   */
  @Constraints.Required
  private Long timestamp;

  /**
   * Duration of the external event in milliseconds since the epoch.
   */
  @Constraints.Required
  private Long duration;

  /**
   * Each external event may be mapped to multiple alerts.
   *
   * For instance, if a tropical storm comes through, that storm may cause many alerts in a small geographical area.
   */
  @OneToMany(mappedBy = "externalEvent", cascade = CascadeType.ALL)
  private List<Alert> alerts = new ArrayList<>();

  /**
   * Convenience constructor for creating an external event.
   * @param eventType The type of the event, weather, man-made, etc.
   * @param eventDescription The description of the event.
   * @param timestamp Timestamp of the event in milliseconds since the epoch.
   * @param duration Duration of the event in milliseconds since the epoch.
   */
  public ExternalEvent(String eventType, String eventDescription, Long timestamp, Long duration) {
    this.setEventType(eventType);
    this.setEventDescription(eventDescription);
    this.setTimestamp(timestamp);
    this.setDuration(duration);
  }

  /**
   * Creates a finder for filtering specific events from DB.
   * @return A new finder for ExternalEvents.
   */
  public static Finder<Long, ExternalEvent> find() {
    return new Finder<>(Long.class, ExternalEvent.class);
  }

  /**
   * Get the primary key.
   * @return The primary key.
   */
  public Long getPrimaryKey() {
    return primaryKey;
  }

  /**
   * Sets the primary key.
   * @param primaryKey The primary key.
   */
  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Gets the event type.
   * @return The event type.
   */
  public String getEventType() {
    return eventType;
  }

  /**
   * Sets the event type.
   * @param eventType The event type.
   */
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  /**
   * Gets the event description.
   * @return The event description.
   */
  public String getEventDescription() {
    return eventDescription;
  }

  /**
   * Sets the event description.
   * @param eventDescription The event description.
   */
  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }

  /**
   * Gets the timestamp of the event in milliseconds since the epoch.
   * @return Timestamp in milliseconds since the epoch.
   */
  public Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp of the event.
   * @param timestamp The number of milliseconds since the epoch.
   */
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Get the duration of the external event in milliseconds.
   * @return Duration of the event in milliseconds.
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Set the duration of the event.
   * @param duration Duration of event (in milliseconds).
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Get alerts associated with an event.
   * @return Alerts associated with this event.
   */
  public List<Alert> getAlerts() {
    return alerts;
  }

  /**
   * Set alerts associated with this event.
   * @param alerts Alerts associated with this event.
   */
  public void setAlerts(List<Alert> alerts) {
    this.alerts = alerts;
  }
}
