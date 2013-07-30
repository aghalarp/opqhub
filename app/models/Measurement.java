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
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Measurement extends Model {
  @Id
  private Long primaryKey;

  @Required
  private Long timestamp;

  @Required
  private Double voltage;

  @Required
  private Double frequency;

  @Required
  @ManyToOne(cascade = CascadeType.ALL)
  private OpqDevice device;

  public Measurement(Long timestamp, Double voltage, Double frequency) {
    this.setTimestamp(timestamp);
    this.setVoltage(voltage);
    this.setFrequency(frequency);
  }

  public static Finder<Long, Measurement> find() {
    return new Finder<>(Long.class, Measurement.class);
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Double getVoltage() {
    return voltage;
  }

  public void setVoltage(Double voltage) {
    this.voltage = voltage;
  }

  public Double getFrequency() {
    return frequency;
  }

  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  public OpqDevice getDevice() {
    return this.device;
  }

  public void setDevice(OpqDevice device) {
    this.device = device;
  }
}
