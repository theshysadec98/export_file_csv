package rmhub.mod.measurementtraffic.model.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table(name = "traffic_information")
public class TrafficInformation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  private Long id;

  @Column(name = "saturation")
  private Integer saturation;

  @Column(name = "speed")
  private Integer speed;

  @Column(name = "vehicles")
  private Integer vehicles;

  @Column(name = "created_date")
  @CreatedDate
  private Timestamp createdDate;

  @Column(name = "period_date")
  private Timestamp periodDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id", referencedColumnName = "id")
  private Device device;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "direction_lane_id", referencedColumnName = "id")
  private DirectionLane directionLane;
}
