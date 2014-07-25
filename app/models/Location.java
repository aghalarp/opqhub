package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Location extends Model {
  /* ----- Fields ----- */
  @Id
  private Long primaryKey;

  private String gridId;

  private Integer gridScale;

  private Integer gridRow;

  private Integer gridCol;

  private Double northEastLatitude;

  private Double northEastLongitude;

  private Double southWestLatitude;

  private Double getSouthWestLongitude;

  /* ----- Relationships ----- */
  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
  private
  List<OpqDevice> opqDevices = new ArrayList<>();

  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
  private
  List<Event> events = new ArrayList<>();

  public static Finder<Long, Location> find() {
    return new Finder<>(Long.class, Location.class);
  }


  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getGridId() {
    return gridId;
  }

  public void setGridId(String gridId) {
    this.gridId = gridId;
  }

  public Integer getGridScale() {
    return gridScale;
  }

  public void setGridScale(Integer gridScale) {
    this.gridScale = gridScale;
  }

  public Integer getGridRow() {
    return gridRow;
  }

  public void setGridRow(Integer gridRow) {
    this.gridRow = gridRow;
  }

  public Integer getGridCol() {
    return gridCol;
  }

  public void setGridCol(Integer gridCol) {
    this.gridCol = gridCol;
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

  public Double getGetSouthWestLongitude() {
    return getSouthWestLongitude;
  }

  public void setGetSouthWestLongitude(Double getSouthWestLongitude) {
    this.getSouthWestLongitude = getSouthWestLongitude;
  }

  public List<OpqDevice> getOpqDevices() {
    return opqDevices;
  }

  public void setOpqDevices(List<OpqDevice> opqDevices) {
    this.opqDevices = opqDevices;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }
}
