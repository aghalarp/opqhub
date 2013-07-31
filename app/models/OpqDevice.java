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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for manipulating persisted OpqDevices.
 *
 * OPQ devices have an id, description, and optional locational information. OPQ devices tend to link the rest of
 * the persisted object together.
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
   * Short description of the device.
   * I.e. basement, office, etc.
   */
  @Constraints.Required
  private String description;

  // TODO: Figure out rest of constraints
  /**
   * State that the device is located in (not optional).
   */
  private String state;

  /**
   * City that the device is located in (optional).
   */
  private String city;

  /**
   * Zip code that device is located in (optional).
   */
  private String zip;

  /**
   * Street name that the device is located at (optional).
   */
  private String streetName;

  /**
   * Street number that the device is located at (optional).
   */
  private String streetNumber;

  /**
   * Longitude that this device is located at.
   */
  private Double longitude;

  /**
   * Latitude that this device is located at.
   */
  private Double latitude;

  /**
   * Determines if device is participating in CDSI.
   */
  private Boolean participatingInCdsi;

  /**
   * Person that this device is associated with.
   *
   * Each device is associated with one and only one person.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private Person person;

  /**
   * Measurements that this device is associated with.
   *
   * Each device can be associated with many measurements.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Measurement> measurements = new ArrayList<>();

  /**
   * Alert notifications that this device is associated with.
   *
   * Each device can be associated with many alert notifications.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<AlertNotification> alertNotifications = new ArrayList<>();

  /**
   * Alerts that this device is associated with.
   *
   * Each device can be associated with many alerts.
   */
  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Alert> alerts = new ArrayList<>();

  /**
   * Convenience constructor for test package.
   * @param deviceId Id of the device as 16 hex digits.
   * @param description Short description of the device.
   * @param state State that device is located in.
   */
  public OpqDevice(Long deviceId, String description, String state) {
    this.setDeviceId(deviceId);
    this.setDescription(description);
    this.setState(state);
  }

  /**
   * Finder for filtering persisted devices.
   * @return Finder for filtering persisted devices.
   */
  public static Finder<Long, OpqDevice> find() {
    return new Finder<>(Long.class, OpqDevice.class);
  }

  /**
   * Gets the primary key.
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
   * Gets the device id.
   * @return The device id (unique 64-bit int).
   */
  public Long getDeviceId() {
    return deviceId;
  }

  /**
   * Sets the device id.
   * @param deviceId A unique 64-bit int.
   */
  public void setDeviceId(Long deviceId) {
    this.deviceId = deviceId;
  }

  /**
   * Gets the short description of the device.
   * @return Short description of device.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the short description of the device.
   * @param description Short description of the device.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the state that this device resides in.
   * @return State this this device resides in.
   */
  public String getState() {
    return state;
  }

  /**
   * Sets the state that this device resides in.
   * @param state State this this device resides in.
   */
  public void setState(String state) {
    this.state = state;
  }

  /**
   * Gets the city that this device resides in.
   * @return The city that this device resides in.
   */
  public String getCity() {
    return city;
  }

  /**
   * Sets the city that this device resides in.
   * @param city City that this device resides in.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Gets the zip that this device resides in.
   * @return Zip that this device resides in.
   */
  public String getZip() {
    return zip;
  }

  /**
   * Sets the zip that this device resides in.
   * @param zip Zip that this device resides in.
   */
  public void setZip(String zip) {
    this.zip = zip;
  }

  /**
   * Gets the street name that this device resides on.
   * @return Street name that this device resides on.
   */
  public String getStreetName() {
    return streetName;
  }

  /**
   * Sets the street name that this device resides on.
   * @param streetName Street name that this device resides on.
   */
  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  /**
   * Gets the street number that this device resides on.
   * @return Street number that this device resides on.
   */
  public String getStreetNumber() {
    return streetNumber;
  }

  /**
   * Sets the street number that this device resides on.
   * @param streetNumber Street number that this device resides on.
   */
  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }

  /**
   * Gets the longitude that this device resides at.
   * @return Longitude that this device resides at.
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude that this device resides at.
   * @param longitude Longitude that this device resides at.
   */
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   * Gets the latitude that this device resides at.
   * @return Latitude that this device resides at.
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude that this device resides at.
   * @param latitude The latitude that this device resides at.
   */
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   * Gets whether or not this device is participating in CDSI.
   * @return Participating in CDSI.
   */
  public Boolean getParticipatingInCdsi() {
    return this.participatingInCdsi;
  }

  /**
   * Set whether or not this device is participating in CDSI.
   * @param participatingInCdsi Participating in CDSI.
   */
  public void setParticipatingInCdsi(Boolean participatingInCdsi) {
    this.participatingInCdsi = participatingInCdsi;
  }

  /**
   * Get the person associated with this device.
   * @return Person associated with this device.
   */
  public Person getPerson() {
    return person;
  }

  /**
   * Set the person associated with this device.
   * @param person Person associated with this device.
   */
  public void setPerson(Person person) {
    this.person = person;
  }

  /**
   * Get measurements associated with this device.
   * @return Measurements associated with this device.
   */
  public List<Measurement> getMeasurements() {
    return measurements;
  }

  /**
   * Set measurements associated with this device.
   * @param measurements Measurements associated with this device.
   */
  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }

  /**
   * Get alert notifications associated with this device.
   * @return Alert notifications associated with this device.
   */
  public List<AlertNotification> getAlertNotifications() {
    return alertNotifications;
  }

  /**
   * Set alert notifications associated with this device.
   * @param alertNotifications Alert notifications associated with this device.
   */
  public void setAlertNotifications(List<AlertNotification> alertNotifications) {
    this.alertNotifications = alertNotifications;
  }

  /**
   * Get alerts associated with this device.
   * @return Alerts associated with this device.
   */
  public List<Alert> getAlerts() {
    return alerts;
  }

  /**
   * Set alerts associated with this device.
   * @param alerts Alerts associated with this device.
   */
  public void setAlerts(List<Alert> alerts) {
    this.alerts = alerts;
  }
}
