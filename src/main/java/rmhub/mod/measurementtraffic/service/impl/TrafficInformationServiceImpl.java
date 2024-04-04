package rmhub.mod.measurementtraffic.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.entity.TrafficInformation;
import rmhub.mod.measurementtraffic.model.ExportRequest;
import rmhub.mod.measurementtraffic.model.request.TrafficInformationRequest;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;
import rmhub.mod.measurementtraffic.model.DirectionRequest;
import rmhub.mod.measurementtraffic.model.response.FilterResponse;
import rmhub.mod.measurementtraffic.model.response.RestResult;
import rmhub.mod.measurementtraffic.model.TrafficMeasurementResponse;
import rmhub.mod.measurementtraffic.model.DeviceMeasurementResponse;
import rmhub.mod.measurementtraffic.model.DirectionMeasurementResponse;
import rmhub.mod.measurementtraffic.model.LaneMeasurementResponse;
import rmhub.mod.measurementtraffic.repository.TrafficInformationRepository;
import rmhub.mod.measurementtraffic.service.TrafficInformationService;


import static rmhub.mod.measurementtraffic.model.response.RestResult.STATUS_ERROR;
import static rmhub.mod.measurementtraffic.model.response.RestResult.STATUS_SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficInformationServiceImpl implements TrafficInformationService {

  private static final int PERIOD_TIME = 1;

  @Value("${time.zone}")
  private String timeZone;

  private final TrafficInformationRepository repository;

  @Override
  public List<TrafficInformation> create(Device device, List<DirectionLane> directionLanes, List<TrafficInformationRequest> requests) {
    if (CollectionUtils.isEmpty(requests)) {
      return new ArrayList<>();
    }
    List<TrafficInformation> trafficInformationList = requests.stream()
        .map(request -> convertRequestToEntity(device, directionLanes, request))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(trafficInformationList)) {
      log.info("There is no traffic information to save");
      return new ArrayList<>();
    }
    return repository.saveAll(trafficInformationList);
  }

  @Override
  public FilterResponse<TrafficMeasurementResponse> filter(TrafficSearchRequest searchRequest, boolean hasPage) {
    Sort sort = generateSort(searchRequest);
    Pageable pageable = hasPage ? PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort)
            : PageRequest.of(0, Integer.MAX_VALUE, sort);
    Page<Timestamp> result = repository.filterPeriodDate(searchRequest, pageable);
    if (result == null) {
      return FilterResponse.<TrafficMeasurementResponse>builder()
          .data(null)
          .build();
    }
    List<String> directions = null;
    List<String> lanes = null;
    List<String> devices = null;
    if (!CollectionUtils.isEmpty(searchRequest.getDirections())) {
      directions = searchRequest.getDirections().stream().map(DirectionRequest::getName).collect(Collectors.toList());
      lanes = searchRequest.getDirections().stream().map(DirectionRequest::getLane).flatMap(List::stream).collect(Collectors.toList());
    }
    if(!CollectionUtils.isEmpty(searchRequest.getDeviceNames()) && !searchRequest.getClass().equals(TrafficSearchRequest.class)){
      devices = searchRequest.getDeviceNames();
    }
    List<MeasurementInformation> measurements = repository.findMeasurements(result.getContent(), directions, lanes, devices);
    List<TrafficMeasurementResponse> trafficMeasurementResponses = generateTrafficMeasurementResponses(result.getContent(), measurements);
    return FilterResponse.<TrafficMeasurementResponse>builder()
        .data(trafficMeasurementResponses)
        .page(result.getTotalPages())
        .size(result.getSize())
        .pageElements(result.getContent().size())
        .totalElements(result.getTotalElements())
        .build();
  }

  @Override
  public RestResult<Resource> exportCSV(ExportRequest exportRequest) {
    if( StringUtils.isEmpty(exportRequest.getFromTimestamp()) && StringUtils.isEmpty(exportRequest.getToTimestamp())){
      return RestResult.<Resource>builder()
              .status(STATUS_ERROR)
              .messages(Collections.singletonList("Start time and end time null."))
              .build();
    }
    if( StringUtils.isEmpty(exportRequest.getFromTimestamp()) ){
      exportRequest.setFromTimestamp(exportRequest.getToTimestamp() -  Long.parseLong(limitedTime) + 10000);
    }
    if( StringUtils.isEmpty(exportRequest.getToTimestamp()) ){
      exportRequest.setToTimestamp(exportRequest.getFromTimestamp() + Long.parseLong(limitedTime) -10000);
    }
    if( checkTime( exportRequest.getFromTimestamp(), exportRequest.getToTimestamp())){
      return RestResult.<Resource>builder()
              .status(STATUS_ERROR)
              .messages(Collections.singletonList("There is no data for the selected time period."))
              .build();
    }
    TrafficSearchRequest searchRequest = TrafficSearchRequest.builder()
            .deviceNames(exportRequest.getDeviceNames())
            .highway(exportRequest.getHighway())
            .fromTimestamp(exportRequest.getFromTimestamp())
            .toTimestamp(exportRequest.getToTimestamp())
            .orders(exportRequest.getOrders())
            .build();
    FilterResponse<TrafficMeasurementResponse> result = filter(searchRequest, false);
    List<TrafficMeasurementResponse> measurementResponses = result.getData();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try  (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT)){
      csvPrinter.printRecord("Period date",
              "Device id",
              "Direction name",
              "Lane",
              "Speed",
              "Saturation",
              "Vehicles");
      for ( TrafficMeasurementResponse data: measurementResponses) {
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        for (DeviceMeasurementResponse dataDevice: data.getDeviceMeasurements()) {
          for (DirectionMeasurementResponse dataDirection : dataDevice.getDirections()) {
            for ( LaneMeasurementResponse dataLane: dataDirection.getLanes()) {
              csvPrinter.printRecord(formatter.format(new Date(data.getTimestamp() + Long.parseLong(timeZone))),
                      dataDevice.getName(),
                      dataDirection.getName(),
                      dataLane.getName(),
                      dataLane.getSaturation(),
                      dataLane.getSpeed(),
                      dataLane.getVehicles());
            }
          }
        }
      }
      csvPrinter.flush();
    } catch (IOException e) {
      log.error("Error While writing CSV ", e);
    }
    return RestResult.<Resource>builder()
            .data(new ByteArrayResource(outputStream.toByteArray()))
            .status(STATUS_SUCCESS)
            .build();
  }

  private Sort generateSort(TrafficSearchRequest searchRequest) {
    if (CollectionUtils.isEmpty(searchRequest.getOrders())) {
      Order order = new Order(Direction.DESC, "period_date");
      return Sort.by(Collections.singletonList(order));
    }
    List<Order> orders = new ArrayList<>();
    for (Map.Entry<String, String> entry: searchRequest.getOrders().entrySet()) {
      Direction direction = Direction.fromString(entry.getValue());
      Order order = new Order(direction, entry.getKey());
      orders.add(order);
    }
    return Sort.by(orders);
  }

  private List<TrafficMeasurementResponse> generateTrafficMeasurementResponses(List<Timestamp> standardTimestamps, List<MeasurementInformation> measurements) {
    if (CollectionUtils.isEmpty(measurements)) {
      return new ArrayList<>();
    }
    return standardTimestamps.stream().map(timestamp -> {
      List<MeasurementInformation> measurementInformationList = measurements.stream()
          .filter(measurement -> timestamp.equals(measurement.getPeriodDate()))
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(measurementInformationList)) {
        return TrafficMeasurementResponse.builder()
            .timestamp(timestamp.getTime())
            .build();
      }
      return TrafficMeasurementResponse.builder()
          .timestamp(timestamp.getTime())
          .deviceMeasurements(generateDeviceMeasurements(measurementInformationList))
          .build();
    }).collect(Collectors.toList());
  }

  private List<DeviceMeasurementResponse> generateDeviceMeasurements(List<MeasurementInformation> measurements) {
    List<DeviceMeasurementResponse> responses = new ArrayList<>();
    Map<String, List<MeasurementInformation>> measurementMap = measurements.stream().collect(Collectors.groupingBy(MeasurementInformation::getOriginId));
    //
    measurementMap.forEach((deviceName, measurementInformationList) -> {
      if (CollectionUtils.isEmpty(measurementInformationList)) {
        return;
      }
      responses.add(
          DeviceMeasurementResponse.builder()
              .name(deviceName)
              .directions(generateDirectionMeasurements(measurementInformationList))
              .build()
      );
    });
    return responses;
  }

  private List<DirectionMeasurementResponse> generateDirectionMeasurements(List<MeasurementInformation> measurements) {
    List<DirectionMeasurementResponse> responses = new ArrayList<>();
    Map<String, List<MeasurementInformation>> measurementMap = measurements.stream().collect(Collectors.groupingBy(MeasurementInformation::getDirection));
    measurementMap.forEach((direction, measurementInformationList) -> {
      if (CollectionUtils.isEmpty(measurementInformationList)) {
        return;
      }
      responses.add(
          DirectionMeasurementResponse.builder()
              .name(direction)
              .lanes(generateLaneMeasurements(measurementInformationList))
              .build()
      );
    });
    return responses;
  }

  private List<LaneMeasurementResponse> generateLaneMeasurements(List<MeasurementInformation> measurements) {
    List<LaneMeasurementResponse> responses = new ArrayList<>();
    Map<String, List<MeasurementInformation>> measurementMap = measurements.stream().collect(Collectors.groupingBy(MeasurementInformation::getLane));
    measurementMap.forEach((lane, measurementInformationList) -> {
      if (CollectionUtils.isEmpty(measurementInformationList)) {
        return;
      }
      MeasurementInformation saturationInfo = measurements.stream().filter(measurement -> measurement.getSaturation() != null).findFirst().orElse(null);
      Integer saturation = saturationInfo == null ? null : saturationInfo.getSaturation();
      MeasurementInformation speedInfo = measurements.stream().filter(measurement -> measurement.getSpeed() != null).findFirst().orElse(null);
      Integer speed = speedInfo == null ? null : speedInfo.getSpeed();
      MeasurementInformation vehiclesInfo = measurements.stream().filter(measurement -> measurement.getVehicles() != null).findFirst().orElse(null);
      Integer vehicles = vehiclesInfo == null ? null : vehiclesInfo.getVehicles();
      responses.add(
        LaneMeasurementResponse.builder()
            .name(lane)
            .saturation(saturation)
            .speed(speed)
            .vehicles(vehicles)
            .build()
      );
    });
    return responses;
  }

  private TrafficInformation convertRequestToEntity(Device device, List<DirectionLane> directionLanes, TrafficInformationRequest request) {
    DirectionLane directionLane = directionLanes.stream()
        .filter(entity -> entity.getDirection().equals(request.getDirection()) && entity.getLane().equals(request.getLane()))
        .findFirst()
        .orElse(null);
    if (directionLane == null) {
      return null;
    }
    TrafficInformation trafficInformation = new TrafficInformation();
    trafficInformation.setDevice(device);
    trafficInformation.setDirectionLane(directionLane);
    trafficInformation.setSaturation(request.getSaturation());
    trafficInformation.setSpeed(request.getSpeed());
    trafficInformation.setVehicles(request.getVehicles());
    Timestamp createdDate = request.getCreatedDate() == null ? new Timestamp(System.currentTimeMillis()) : request.getCreatedDate();
    trafficInformation.setCreatedDate(createdDate);
    trafficInformation.setPeriodDate(standardizeTimestamp(createdDate));

    return trafficInformation;
  }

  private Timestamp standardizeTimestamp(Timestamp timestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(timestamp);
    int minute = calendar.get(Calendar.MINUTE);
    int standardMinute = (minute / PERIOD_TIME) * PERIOD_TIME;
    calendar.set(Calendar.MINUTE, standardMinute);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return new Timestamp(calendar.getTime().getTime());
  }

  @Value("${data.export.time}")
  private String limitedTime;

  private boolean checkTime(Long start, Long end){
    return (end - start > Long.parseLong(limitedTime)) || end < start;
  }
}
