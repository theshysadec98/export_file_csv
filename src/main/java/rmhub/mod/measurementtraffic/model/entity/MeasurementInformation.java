package rmhub.mod.measurementtraffic.model.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeasurementInformation {
  private String originId;
  private String deviceName;
  private String direction;
  private String lane;
  private Integer saturation;
  private Integer speed;
  private Integer vehicles;
  private Date periodDate;
}
