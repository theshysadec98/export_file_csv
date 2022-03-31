package rmhub.mod.measurementtraffic.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeviceRequest {

  private String highway;

  private String name;
}
