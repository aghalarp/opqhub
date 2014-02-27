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

import com.avaje.ebean.validation.NotNull;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for manipulating persisted OpqDevices.
 * <p/>
 * OPQ devices have an id, description, and optional locational information. OPQ devices tend to link the rest of the
 * persisted object together.
 */
@Entity
public class OpqDevice extends Model {
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
   * Determines if device is participating in CDSI.
   */
  @NotNull
  private Boolean sharingData;

  /**
   * The id of the grid square associated with this device.
   */
  private String gridId;

  /**
   * The length of all sides of a grid square in km.
   */
  private Double gridScale;

  /**
   * The row within the grid this device is associated.
   */
  private Integer gridRow;

  /**
   * The column within the grid this device is associated.
   */
  private Integer gridCol;

  /**
   * The following latitude and longitudes can be used to make up the bounding box of the grid square that this
   * device resides in.
   */
  private Double northEastLatitude;
  private Double northEastLongitude;
  private Double southWestLatitude;
  private Double southWestLongitude;

  /**
   * Person that this device is associated with.
   * <p/>
   * Each device is associated with one and only one person.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private Person person;

  /**
   * Measurements that this device is associated with.
   * <p/>
   * Each device can be associated with many measurements.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Measurement> measurements = new ArrayList<>();

  /**
   * Event notifications that this device is associated with.
   * <p/>
   * Each device can be associated with many alert notifications.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<AlertNotification> alertNotifications = new ArrayList<>();

  /**
   * Alerts that this device is associated with.
   * <p/>
   * Each device can be associated with many events.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Event> events = new ArrayList<>();

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
   * Gets whether or not this device is participating in CDSI.
   *
   * @return Participating in CDSI.
   */
  public Boolean getSharingData() {
    return this.sharingData;
  }

  /**
   * Returns the gridId.
   * @return The gridId.
   */
  public String getGridId() {
    return gridId;
  }

  /**
   * Sets the gridId.
   * @param gridId The gridId.
   */
  public void setGridId(String gridId) {
    this.gridId = gridId;
  }

  /**
   * Set whether or not this device is participating in CDSI.
   *
   * @param sharingData Participating in CDSI.
   */
  public void setSharingData(Boolean sharingData) {
    this.sharingData = sharingData;
  }

  /**
   * Get the person associated with this device.
   *
   * @return Person associated with this device.
   */
  public Person getPerson() {
    return person;
  }

  /**
   * Set the person associated with this device.
   *
   * @param person Person associated with this device.
   */
  public void setPerson(Person person) {
    this.person = person;
  }

  /**
   * Get measurements associated with this device.
   *
   * @return Measurements associated with this device.
   */
  public List<Measurement> getMeasurements() {
    return measurements;
  }

  /**
   * Set measurements associated with this device.
   *
   * @param measurements Measurements associated with this device.
   */
  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }

  /**
   * Get alert notifications associated with this device.
   *
   * @return Event notifications associated with this device.
   */
  public List<AlertNotification> getAlertNotifications() {
    return alertNotifications;
  }

  /**
   * Set alert notifications associated with this device.
   *
   * @param alertNotifications Event notifications associated with this device.
   */
  public void setAlertNotifications(List<AlertNotification> alertNotifications) {
    this.alertNotifications = alertNotifications;
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


  public Double getNorthEastLatitude() {
    return northEastLatitude;
  }

  public void setNorthEastLatitude(Double northEastLatitude) {
    this.northEastLatitude = northEastLatitude;
  }

  public Double getNorthEastLongitude() {
    return northEastLongitude;
  }

  public void setNorthEastLongitude(Double northEastLongitude) {
    this.northEastLongitude = northEastLongitude;
  }

  public Double getSouthWestLatitude() {
    return southWestLatitude;
  }

  public void setSouthWestLatitude(Double southWestLatitude) {
    this.southWestLatitude = southWestLatitude;
  }

  public Double getSouthWestLongitude() {
    return southWestLongitude;
  }

  public void setSouthWestLongitude(Double southWestLongitude) {
    this.southWestLongitude = southWestLongitude;
  }

  /**
   * The scale of each grid square (length of side in km).
   */
  public Double getGridScale() {
    return gridScale;
  }

  public void setGridScale(Double gridScale) {
    this.gridScale = gridScale;
  }

  /**
   * The row of the grid this device is associated with.
   */
  public Integer getGridRow() {
    return gridRow;
  }

  public void setGridRow(Integer gridRow) {
    this.gridRow = gridRow;
  }

  /**
   * The column of the grid this device is associated with.
   */
  public Integer getGridCol() {
    return gridCol;
  }

  public void setGridCol(Integer gridCol) {
    this.gridCol = gridCol;
  }
}
