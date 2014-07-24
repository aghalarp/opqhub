package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Key extends Model {
  /* ----- Fields ----- */
  @Id
  private Long primaryKey;

  @Constraints.Required
  private String key;

  /* ----- Relationships ----- */
  @ManyToMany(mappedBy = "keys", cascade = CascadeType.ALL)
  private List<Person> persons = new ArrayList<>();

  @OneToOne
  private OpqDevice opqDevice;

  @OneToMany(mappedBy = "key", cascade = CascadeType.ALL)
  private List<Event> events = new ArrayList<>();

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }

  public OpqDevice getOpqDevice() {
    return opqDevice;
  }

  public void setOpqDevice(OpqDevice opqDevice) {
    this.opqDevice = opqDevice;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }
}
