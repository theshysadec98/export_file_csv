package rmhub.mod.measurementtraffic.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.request.DirectionLaneRequest;
import rmhub.mod.measurementtraffic.repository.DirectionLaneRepository;
import rmhub.mod.measurementtraffic.service.DirectionLaneService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectionLaneServiceImpl implements DirectionLaneService {

  private DirectionLaneRepository repository;

  @Override
  public List<DirectionLane> getOrCreate(List<DirectionLaneRequest> requests) {
    if (CollectionUtils.isEmpty(requests)) {
      return Collections.emptyList();
    }

    boolean isValidRequest = validateRequests(requests);
    if (!isValidRequest) {
      return Collections.emptyList();
    }

    List<DirectionLane> currentDirectionLaneList = getCurrentDirectionLanes(requests);
    List<DirectionLaneRequest> createdDirectionLaneRequestList = requests.stream()
        .filter(request -> !isCurrentDirectionLane(currentDirectionLaneList, request))
        .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(createdDirectionLaneRequestList)) {
      return currentDirectionLaneList;
    }
    List<DirectionLane> createdDirectionLaneList = createdDirectionLaneRequestList.stream().map(request -> {
      DirectionLane directionLane = new DirectionLane();
      directionLane.setDirection(request.getDirection());
      directionLane.setLane(request.getLane());
      return directionLane;
    }).collect(Collectors.toList());
    createdDirectionLaneList = repository.saveAll(createdDirectionLaneList);

    if (CollectionUtils.isEmpty(createdDirectionLaneList)) {
      return currentDirectionLaneList;
    }

    return Stream.concat(currentDirectionLaneList.stream(), createdDirectionLaneList.stream()).collect(Collectors.toList());
  }

  private boolean isCurrentDirectionLane(List<DirectionLane> directionLaneList, DirectionLaneRequest request) {
    for (DirectionLane directionLane: directionLaneList) {
      if (directionLane.getDirection().equals(request.getDirection()) && directionLane.getLane().equals(request.getLane())) {
        return true;
      }
    }
    return false;
  }

  private boolean validateRequests(List<DirectionLaneRequest> requests) {
    for (DirectionLaneRequest request: requests) {
      if (StringUtils.isEmpty(request.getDirection()) || StringUtils.isEmpty(request.getLane())) {
        log.error("Direction and lane must be not null");
        return false;
      }
    }
    return true;
  }

  private List<DirectionLane> getCurrentDirectionLanes(List<DirectionLaneRequest> requests) {
    // maybe better by customization query select
    List<DirectionLane> currentDirectionLaneList = new ArrayList<>();
    requests.forEach(request -> {
      DirectionLane directionLane = repository.findFirstByDirectionAndLane(request.getDirection(), request.getLane());
      if (directionLane != null) {
        currentDirectionLaneList.add(directionLane);
      }
    });
    return currentDirectionLaneList;
  }
}
