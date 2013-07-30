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

import com.avaje.ebean.validation.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Person extends Model {
  @Id
  private long primaryKey;

  @Required
  private String firstName;

  @Required
  private String lastName;

  @Required
  @Email
  private String email;

  @Required
  private String passwordHash;

  @Required
  private String state;

  private String city;
  private String zip;
  private String streetName;
  private String streetNumber;

  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
  private List<OpqDevice> devices = new ArrayList<>();

  public Person(String firstName, String lastName, String email, String passwordHash, String state, String city,
                String zip, String streetName, String streetNumber) {
    this.setFirstName(firstName);
    this.setLastName(lastName);
    this.setEmail(email);
    this.setPasswordHash(passwordHash);
    this.setState(state);
    this.setCity(city);
    this.setZip(zip);
    this.setStreetName(streetName);
    this.setStreetNumber(streetNumber);
  }

  public static Finder<Long, Person> find() {
    return new Finder<>(Long.class, Person.class);
  }

  public long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
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

  public List<OpqDevice> getDevices() {
    return devices;
  }

  public void setDevices(List<OpqDevice> devices) {
    this.devices = devices;
  }
}
