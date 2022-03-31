package rmhub.mod.measurementtraffic.service;

import java.util.List;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.request.DirectionLaneRequest;

public interface DirectionLaneService {

  List<DirectionLane> getOrCreate(List<DirectionLaneRequest> requests);
}
