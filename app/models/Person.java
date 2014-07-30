/*
  This file is part of OPQHub.

  opa-ao is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opa-ao is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with OPQHub.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2013 Anthony Christe
 */

package models;

import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.mvc.Controller;
import utils.Sms;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains methods relating to the persisted Person entity.
 */
@Entity
public class Person extends Model {
  /* ----- Fields ----- */
  /**
   * Primary key.
   */
  @Id
  private long primaryKey;

  /**
   * First name.
   */
  @Required
  private String firstName;

  /**
   * Last name.
   */
  @Required
  private String lastName;

  /**
   * E-mail address.
   */
  @Constraints.Email
  @Required
  private String email;

  /**
   * Password. The password itself should never be stored to the DB. This field is simply used as a temp. location while
   * the password hash is created.
   */
  private String password;

  /**
   * Password hash.
   */
  @Required
  private byte[] passwordHash;

  /**
   * Password salt. The salt is a random 32 byte value generated at account creating or password update.
   */
  @Required
  private byte[] passwordSalt;

  @Constraints.Email
  private String alertEmail;

  private Sms.SmsCarrier smsCarrier;

  private String smsNumber;

  /* ----- Relationships ----- */
  @ManyToMany(mappedBy = "persons", cascade = CascadeType.ALL)
  private Set<AccessKey> accessKeys = new HashSet<>();

  /**
   * Convenience constructor for creating Person objects while testing.
   *
   * @param firstName    The first name of the person.
   * @param lastName     The last name of the person.
   * @param email        The email of the person.
   * @param passwordHash The password hash of the person.
   */
  public Person(String firstName, String lastName, String email, byte[] passwordHash) {
    this.setFirstName(firstName);
    this.setLastName(lastName);
    this.setEmail(email);
    this.setPasswordHash(passwordHash);
  }



  /**
   * Finder for filtering persisted person entities.
   *
   * @return New finder for filtering persisted person entities.
   */
  public static Finder<Long, Person> find() {
    return new Finder<>(Long.class, Person.class);
  }

  public static Person getLoggedIn() {
    return Person.find().where().eq("email", Controller.session("email")).findUnique();
  }

  /**
   * Gets the primary key.
   *
   * @return The primary key.
   */
  public long getPrimaryKey() {
    return primaryKey;
  }

  /**
   * Sets the primary key.
   *
   * @param primaryKey The primary key.
   */
  public void setPrimaryKey(long primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Gets the first name.
   *
   * @return Person's first name.
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets person's first name.
   *
   * @param firstName Person's first name.
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the person's last name.
   *
   * @return Person's last name.
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the person's last name.
   *
   * @param lastName Person's last name.
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the person's e-mail.
   *
   * @return The person's e-mail.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the person's e-mail.
   *
   * @param email Person's e-mail.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Return the password.
   *
   * @return The person.
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Uses the supplied password to create a password hash.
   *
   * @param password The plain-text password to generate a hash for.
   */
  public void setPassword(String password) {
    this.password = password;
    this.setPasswordSalt(utils.FormUtils.generateRandomSalt());
    this.setPasswordHash(utils.FormUtils.hashPassword(this.getPassword(), this.getPasswordSalt()));
    this.password = null;
  }

  /**
   * Gets person's password hash.
   *
   * @return Person's password hash.
   */
  public byte[] getPasswordHash() {
    return passwordHash;
  }

  /**
   * Sets the person's password hash.
   *
   * @param passwordHash The password hash for this person.
   */
  public void setPasswordHash(byte[] passwordHash) {
    this.passwordHash = passwordHash;
  }

  /**
   * Returns this person's salt value.
   *
   * @return This person's salt value.
   */
  public byte[] getPasswordSalt() {
    return this.passwordSalt;
  }

  /**
   * Set this person's salt.
   *
   * @param passwordSalt 32-bit random salt.
   */
  public void setPasswordSalt(byte[] passwordSalt) {
    this.passwordSalt = passwordSalt;
  }

  public Set<AccessKey> getAccessKeys() {
    return accessKeys;
  }

  public void setAccessKeys(Set<AccessKey> accessKeys) {
    this.accessKeys = accessKeys;
  }

  public String getAlertEmail() {
    return alertEmail;
  }

  public void setAlertEmail(String alertEmail) {
    this.alertEmail = alertEmail;
  }

  public Sms.SmsCarrier getSmsCarrier() {
    return smsCarrier;
  }

  public void setSmsCarrier(Sms.SmsCarrier smsCarrier) {
    this.smsCarrier = smsCarrier;
  }

  public String getSmsNumber() {
    return smsNumber;
  }

  public void setSmsNumber(String smsNumber) {
    this.smsNumber = smsNumber;
  }
}
