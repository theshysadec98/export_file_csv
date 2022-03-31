package rmhub.mod.measurementtraffic.model.request;

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrafficInformationRequest {

  private String direction;

  private String lane;

  private Integer saturation;

  private Integer speed;

  private Integer vehicles;

  private Timestamp createdDate;
}
