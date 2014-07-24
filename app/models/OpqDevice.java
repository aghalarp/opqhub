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

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Contains methods for manipulating persisted OpqDevices.
 * <p/>
 * OPQ devices have an id, description, and optional locational information. OPQ devices tend to link the rest of the
 * persisted object together.
 */
@Entity
public class OpqDevice extends Model {
  /* ----- Fields ----- */
  /**
   * The primary key.
   */
  @Id
  private Long primaryKey;

  /**
   * The device id as a 64-bit integer.
   */
  @Constraints.Required
  private Long deviceId;

  /**
   * Short description of the device. I.e. basement, office, etc.
   */
  @Constraints.Required
  private String description;

  /**
   * Determines if device is participating in sharing data.
   */
  private Boolean sharingData;

  private Long lastHeartbeat;

  private String lastKnownIp;

  /* ----- Relationships ----- */

  /**
   * Convenience constructor for test package.
   *
   * @param deviceId    Id of the device as 16 hex digits.
   * @param description Short description of the device.
   */
  public OpqDevice(Long deviceId, String description) {
    this.setDeviceId(deviceId);
    this.setDescription(description);
  }

  /**
   * Finder for filtering persisted devices.
   *
   * @return Finder for filtering persisted devices.
   */
  public static Finder<Long, OpqDevice> find() {
    return new Finder<>(Long.class, OpqDevice.class);
  }

  /**
   * Gets the primary key.
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
   * Gets the device id.
   *
   * @return The device id (unique 64-bit int).
   */
  public Long getDeviceId() {
    return this.deviceId;
  }

  /**
   * Sets the device id.
   *
   * @param deviceId A unique 64-bit represented as a String.
   */
  public void setDeviceId(Long deviceId) {
    this.deviceId = deviceId;
  }

  /**
   * Gets the short description of the device.
   *
   * @return Short description of device.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the short description of the device.
   *
   * @param description Short description of the device.
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Gets whether or not this device is participating in sharing data.
   *
   * @return Participating in sharing data.
   */
  public Boolean getSharingData() {
    return this.sharingData;
  }


  /**
   * Set whether or not this device is participating in sharing data.
   *
   * @param sharingData Participating in sharing data.
   */
  public void setSharingData(Boolean sharingData) {
    this.sharingData = sharingData;
  }


  /**
   * Get events associated with this device.
   *
   * @return Alerts associated with this device.
   */
  public List<Event> getEvents() {
    return events;
  }

  /**
   * Set events associated with this device.
   *
   * @param events Alerts associated with this device.
   */
  public void setEvents(List<Event> events) {
    this.events = events;
  }


  public Long getLastHeartbeat() {
    return lastHeartbeat;
  }

  public void setLastHeartbeat(Long lastHeartbeat) {
    this.lastHeartbeat = lastHeartbeat;
  }

  public String getLastKnownIp() {
    return lastKnownIp;
  }

  public void setLastKnownIp(String lastKnownIp) {
    this.lastKnownIp = lastKnownIp;
  }
}
