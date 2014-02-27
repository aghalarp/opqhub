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

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * This contains methods pertaining to persisted measurement objects.
 * <p/>
 * Measures are made by an opq device and contain a voltage, frequency measurement from a single point in time.
 */
@Entity
public class Measurement extends Model {
  /**
   * The primary key.
   */
  @Id
  private Long primaryKey;

  /**
   * Timestamp of measurement (milliseconds since epoch).
   */
  @Required
  private Long timestamp;

  /**
   * Voltage measurement (in Volts).
   */
  @Required
  private Double voltage;

  /**
   * Frequency measurement (in Hertz).
   */
  @Required
  private Double frequency;

  /**
   * The device that this measurement is associated with.
   * <p/>
   * All measurements belong to one and only one device.
   */
  @Required
  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  /**
   * Convenience constructor.
   *
   * @param timestamp Timestamp of measurement (milliseconds since epoch).
   * @param voltage   Voltage of measurement (in Volts).
   * @param frequency Frequency of measurement (in Hertz).
   */
  public Measurement(Long timestamp, Double frequency, Double voltage) {
    this.setTimestamp(timestamp);
    this.setVoltage(voltage);
    this.setFrequency(frequency);
  }

  /**
   * Create a finder object for finding persisted measurements in DB.
   *
   * @return A new finder object for finding persisted measurements.
   */
  public static Finder<Long, Measurement> find() {
    return new Finder<>(Long.class, Measurement.class);
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
   * Gets the timestamp of the measurement.
   *
   * @return Timestamp of measurement (in milliseconds since epoch).
   */
  public Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp of the measurement.
   *
   * @param timestamp Timestamp of the measurement (milliseconds since epoch).
   */
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the measurement's voltage.
   *
   * @return Measurement's voltage (in Volts).
   */
  public Double getVoltage() {
    return voltage;
  }

  /**
   * Sets the voltage for this measurement.
   *
   * @param voltage Voltage (in Volts).
   */
  public void setVoltage(Double voltage) {
    this.voltage = voltage;
  }

  /**
   * Gets the frequency of this measurement.
   *
   * @return The frequency (in Hertz).
   */
  public Double getFrequency() {
    return frequency;
  }

  /**
   * Sets the frequency of this measurement.
   *
   * @param frequency Frequency (in Hertz).
   */
  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  /**
   * Gets the device associated with this measurement.
   *
   * @return Device associated with this measurement.
   */
  public OpqDevice getDevice() {
    return this.device;
  }

  /**
   * Sets the device associated with this measurement.
   *
   * @param device Device associated with this measurement.
   */
  public void setDevice(OpqDevice device) {
    this.device = device;
  }
}
