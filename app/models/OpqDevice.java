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
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class OpqDevice extends Model {
  @Id
  private Long primaryKey;

  @Constraints.Required
  private Long deviceId;

  @Constraints.Required
  private String description;

  // TODO: Figure out rest of constraints
  private String state;
  private String city;
  private String zip;
  private String streetName;
  private String streetNumber;
  private Double longitude;
  private Double latitude;
  private Boolean participatingInCdsi;

  @ManyToOne(cascade = CascadeType.ALL)
  private Person person;

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Measurement> measurements = new ArrayList<>();

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<AlertNotification> alertNotifications = new ArrayList<>();

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  private List<Alert> alerts = new ArrayList<>();

  // TODO: Implement constructor

  public static Finder<Long, OpqDevice> find() {
    return new Finder<>(Long.class, OpqDevice.class);
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(Long deviceId) {
    this.deviceId = deviceId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Boolean getParticipatingInCdsi() {
    return this.participatingInCdsi;
  }

  public void setParticipatingInCdsi(Boolean participatingInCdsi) {
    this.participatingInCdsi = participatingInCdsi;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }

  public List<AlertNotification> getAlertNotifications() {
    return alertNotifications;
  }

  public void setAlertNotifications(List<AlertNotification> alertNotifications) {
    this.alertNotifications = alertNotifications;
  }

  public List<Alert> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<Alert> alerts) {
    this.alerts = alerts;
  }
}
