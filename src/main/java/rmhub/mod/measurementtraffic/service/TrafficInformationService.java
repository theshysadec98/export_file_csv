package rmhub.mod.measurementtraffic.service;

import java.util.List;

import org.springframework.core.io.Resource;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.entity.TrafficInformation;
import rmhub.mod.measurementtraffic.model.ExportRequest;
import rmhub.mod.measurementtraffic.model.request.TrafficInformationRequest;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;
import rmhub.mod.measurementtraffic.model.response.FilterResponse;
import rmhub.mod.measurementtraffic.model.response.RestResult;
import rmhub.mod.measurementtraffic.model.TrafficMeasurementResponse;

public interface TrafficInformationService {

  List<TrafficInformation> create(Device device, List<DirectionLane> directionLanes, List<TrafficInformationRequest> requests);

  FilterResponse<TrafficMeasurementResponse> filter(TrafficSearchRequest searchRequest, boolean hasPage);
  RestResult<Resource> exportCSV(ExportRequest exportRequest);
}
