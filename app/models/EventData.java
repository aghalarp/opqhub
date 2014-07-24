package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class EventData extends Model {
  /* ----- Fields ----- */
  @Id
  private Long primaryKey;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String waveform;

  /* ----- Relationships ----- */
  @NotNull
  @OneToOne
  private Event event;

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getWaveform() {
    return waveform;
  }

  public void setWaveform(String waveform) {
    this.waveform = waveform;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }
}
