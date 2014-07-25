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

  //@Column(columnDefinition = "MEDIUMTEXT")
  //private String waveform;

  private Double[] waveform;

  /* ----- Relationships ----- */
  @OneToOne
  private Event event;

  public EventData(Double[] waveform) {
    this.waveform = waveform;
  }

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Double[] getWaveform() {
    return waveform;
  }

  public void setWaveform(Double[] waveform) {
    this.waveform = waveform;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public static Finder<Long, EventData> find() {
    return new Finder<>(Long.class, EventData.class);
  }
}
