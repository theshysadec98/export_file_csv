package rmhub.mod.measurementtraffic.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "device")
public class Device {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  private Long id;
  @Column(name = "highway", nullable = false)
  private String highway;
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "origin_id")
  private String originId;
}
