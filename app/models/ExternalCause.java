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
 * <p/>
 * External events can be created by users in order to "tag" power events as being caused by some outside event. Maybe a
 * storm came through, a tree hit the power lines, or somebody ran into a telephone pole.
 */
@Entity
public class ExternalCause extends Model {
  /**
   * The primary key.
   */
  @Id
  private Long primaryKey;

  /**
   * The type of event that caused a power alert. I.e. weather, man-made, etc...
   */
  @Constraints.Required
  private String causeType;

  /**
   * A more detailed description of the external event.
   * <p/>
   * I.e., if the event type was weather, the description might be "Tropical Storm Flossie".
   */
  @Constraints.Required
  private String causeDescription;

  /**
   * Each external event may be mapped to multiple events.
   * <p/>
   * For instance, if a tropical storm comes through, that storm may cause many events in a small geographical area.
   */
  @OneToMany(mappedBy = "externalCause", cascade = CascadeType.ALL)
  private List<Event> events = new ArrayList<>();

  /**
   * Convenience constructor for creating an external event.
   *
   * @param eventType        The type of the event, weather, man-made, etc.
   * @param causeDescription The description of the event.
   */
  public ExternalCause(String eventType, String causeDescription) {
    this.setCauseType(eventType);
    this.setCauseDescription(causeDescription);
  }

  /**
   * Creates a finder for filtering specific events from DB.
   *
   * @return A new finder for ExternalEvents.
   */
  public static Finder<Long, ExternalCause> find() {
    return new Finder<>(Long.class, ExternalCause.class);
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
   * Sets the primary key.
   *
   * @param primaryKey The primary key.
   */
  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Gets the event type.
   *
   * @return The event type.
   */
  public String getCauseType() {
    return causeType;
  }

  /**
   * Sets the event type.
   *
   * @param causeType The event type.
   */
  public void setCauseType(String causeType) {
    this.causeType = causeType;
  }

  /**
   * Gets the event description.
   *
   * @return The event description.
   */
  public String getCauseDescription() {
    return causeDescription;
  }

  /**
   * Sets the event description.
   *
   * @param causeDescription The event description.
   */
  public void setCauseDescription(String causeDescription) {
    this.causeDescription = causeDescription;
  }

  /**
   * Get events associated with an event.
   *
   * @return Alerts associated with this event.
   */
  public List<Event> getEvents() {
    return events;
  }

  /**
   * Set events associated with this event.
   *
   * @param events Alerts associated with this event.
   */
  public void setEvents(List<Event> events) {
    this.events = events;
  }
}
