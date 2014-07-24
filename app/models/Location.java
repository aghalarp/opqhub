package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

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
}
