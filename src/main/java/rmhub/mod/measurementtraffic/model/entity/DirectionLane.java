package rmhub.mod.measurementtraffic.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "direction_lane")
public class DirectionLane {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  private Integer id;

  @Column(name = "direction", nullable = false)
  private String direction;

  @Column(name = "lane", nullable = false)
  private String lane;
}
