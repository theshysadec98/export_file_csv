package rmhub.mod.measurementtraffic.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.kafka.consumer.Data;
import rmhub.mod.measurementtraffic.model.kafka.consumer.Measurement;
import rmhub.mod.measurementtraffic.model.request.DeviceRequest;
import rmhub.mod.measurementtraffic.model.request.DirectionLaneRequest;
import rmhub.mod.measurementtraffic.model.request.TrafficInformationRequest;
import rmhub.mod.measurementtraffic.service.DeviceService;
import rmhub.mod.measurementtraffic.service.DirectionLaneService;
import rmhub.mod.measurementtraffic.service.MeasurementTrafficService;
import rmhub.mod.measurementtraffic.service.TrafficInformationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeasurementTrafficServiceImpl implements MeasurementTrafficService {

  private final DeviceService deviceService;

  private final DirectionLaneService directionLaneService;

  private final TrafficInformationService trafficInformationService;

  @Transactional
  @Override
  public void persistMeasurement(Measurement measurement) {
    if (measurement == null) {
      log.error("There is no data of measurement traffic");
      return;
    }
    if (StringUtils.isEmpty(measurement.getHighway()) || StringUtils.isEmpty(measurement.getName())) {
      log.error("There is no data of highway or name");
      return;
    }

    final Device device =  this.deviceService.getOrCreate(getDevice(measurement));
    if (device == null) {
      log.info("Do not get or create device: highway = {}, name = {}", measurement.getHighway(), measurement.getName());
      return;
    }

    final List<DirectionLane> directionLaneList = this.directionLaneService.getOrCreate(getDirectionLaneList(measurement));
    if (CollectionUtils.isEmpty(directionLaneList)) {
      log.info("Do not get or create list of direction lane");
      return;
    }

    final List<TrafficInformationRequest> trafficInformationRequests = this.getTrafficInformationList(measurement);
    this.trafficInformationService.create(device, directionLaneList, trafficInformationRequests);
  }

  private DeviceRequest getDevice(Measurement measurement) {
    return DeviceRequest.builder()
        .highway(measurement.getHighway())
        .name(measurement.getName())
        .build();
  }

  private List<DirectionLaneRequest> getDirectionLaneList(Measurement measurement) {
    if (measurement.getData() == null) {
      log.info("There is no data to store");
      return new ArrayList<>();
    }
    Data data = measurement.getData();
    List<DirectionLaneRequest> directionLaneRequests = new ArrayList<>();

    if (!CollectionUtils.isEmpty(data.getSaturation())) {
      data.getSaturation().forEach(saturation -> addDirectionLaneRequest(directionLaneRequests, saturation.getDirection(), saturation.getLane()));
    }

    if (!CollectionUtils.isEmpty(data.getSpeed())) {
      data.getSpeed().forEach(speed -> addDirectionLaneRequest(directionLaneRequests, speed.getDirection(), speed.getLane()));
    }

    if (!CollectionUtils.isEmpty(data.getVehicles())) {
      data.getVehicles().forEach(vehicle -> addDirectionLaneRequest(directionLaneRequests, vehicle.getDirection(), vehicle.getLane()));
    }

    return directionLaneRequests;
  }

  private void addDirectionLaneRequest(List<DirectionLaneRequest> requests, String direction, String lane) {
    if (StringUtils.isEmpty(direction) || StringUtils.isEmpty(lane)) {
      return;
    }
    DirectionLaneRequest directionLaneRequest = requests.stream()
        .filter(request -> request.getDirection().equals(direction) && request.getLane().equals(lane))
        .findFirst()
        .orElse(null);
    if (directionLaneRequest != null) {
      return;
    }
    requests.add(DirectionLaneRequest.builder()
        .direction(direction)
        .lane(lane)
        .build());
  }

  private List<TrafficInformationRequest> getTrafficInformationList(Measurement measurement) {
    if (measurement.getData() == null) {
      log.info("There is no data to store");
      return new ArrayList<>();
    }

    Data data = measurement.getData();
    List<TrafficInformationRequest> trafficInformationRequests = new ArrayList<>();

    if (!CollectionUtils.isEmpty(data.getSaturation())) {
      data.getSaturation().forEach(saturation -> {
        if (StringUtils.isEmpty(saturation.getDirection()) || StringUtils.isEmpty(saturation.getLane())) {
          return;
        }
        TrafficInformationRequest request = findByDirectionAndLane(trafficInformationRequests, saturation.getDirection(), saturation.getLane());
        if (request == null) {
          request = TrafficInformationRequest.builder()
              .direction(saturation.getDirection())
              .lane(saturation.getLane())
              .saturation(saturation.getValue())
              .createdDate(measurement.getTimestamp())
              .build();
          trafficInformationRequests.add(request);
        } else {
          request.setSaturation(saturation.getValue());
        }
      });
    }

    if (!CollectionUtils.isEmpty(data.getSpeed())) {
      data.getSpeed().forEach(speed -> {
        if (StringUtils.isEmpty(speed.getDirection()) || StringUtils.isEmpty(speed.getLane())) {
          return;
        }
        TrafficInformationRequest request = findByDirectionAndLane(trafficInformationRequests, speed.getDirection(), speed.getLane());
        if (request == null) {
          request = TrafficInformationRequest.builder()
              .direction(speed.getDirection())
              .lane(speed.getLane())
              .speed(speed.getValue())
              .createdDate(measurement.getTimestamp())
              .build();
          trafficInformationRequests.add(request);
        } else {
          request.setSpeed(speed.getValue());
        }
      });
    }

    if (!CollectionUtils.isEmpty(data.getVehicles())) {
      data.getVehicles().forEach(vehicle -> {
        if (StringUtils.isEmpty(vehicle.getDirection()) || StringUtils.isEmpty(vehicle.getLane())) {
          return;
        }
        TrafficInformationRequest request = findByDirectionAndLane(trafficInformationRequests, vehicle.getDirection(), vehicle.getLane());
        if (request == null) {
          request = TrafficInformationRequest.builder()
              .direction(vehicle.getDirection())
              .lane(vehicle.getLane())
              .vehicles(vehicle.getValue())
              .createdDate(measurement.getTimestamp())
              .build();
          trafficInformationRequests.add(request);
        } else {
          request.setVehicles(vehicle.getValue());
        }
      });
    }

    return trafficInformationRequests;
  }

  private TrafficInformationRequest findByDirectionAndLane(List<TrafficInformationRequest> trafficInformationRequests, String direction, String lane) {
    return trafficInformationRequests.stream()
        .filter(request -> request.getDirection().equals(direction) && request.getLane().equals(lane))
        .findFirst()
        .orElse(null);
  }
}
