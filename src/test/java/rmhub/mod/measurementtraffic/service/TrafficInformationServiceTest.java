package rmhub.mod.measurementtraffic.service;

import java.sql.Timestamp;
import java.util.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.CollectionUtils;
import rmhub.mod.measurementtraffic.model.DirectionRequest;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.entity.TrafficInformation;
import rmhub.mod.measurementtraffic.model.ExportRequest;
import rmhub.mod.measurementtraffic.model.request.TrafficInformationRequest;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;
import rmhub.mod.measurementtraffic.model.response.FilterResponse;
import rmhub.mod.measurementtraffic.model.response.RestResult;
import rmhub.mod.measurementtraffic.model.TrafficMeasurementResponse;
import rmhub.mod.measurementtraffic.repository.TrafficInformationRepository;
import rmhub.mod.measurementtraffic.service.impl.TrafficInformationServiceImpl;

import javax.servlet.http.HttpServletResponse;

@SpringBootTest
public class TrafficInformationServiceTest {

  private TrafficInformationService trafficInformationService;

  @Mock
  private TrafficInformationRepository trafficInformationRepository;

  private String highway;
  private String name;

  private String direction;
  private String lane;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    this.trafficInformationService = new TrafficInformationServiceImpl(trafficInformationRepository);
    this.highway = "M33";
    this.name = "RH_12_43";
    this.direction = "1";
    this.lane = "1";
  }

  @Test
  public void testCreateNoRequest() {
    Device device = getDevice();

    DirectionLane directionLane = getDirectionLane();
    List<DirectionLane> directionLaneList = Collections.singletonList(directionLane);

    List<TrafficInformation> trafficInformationList = trafficInformationService.create(device, directionLaneList, null);

    Assert.assertTrue(CollectionUtils.isEmpty(trafficInformationList));
  }

  @Test
  public void testCreate() {
    Device device = getDevice();

    DirectionLane directionLane = getDirectionLane();
    List<DirectionLane> directionLaneList = Collections.singletonList(directionLane);

    TrafficInformationRequest request = TrafficInformationRequest.builder()
        .vehicles(2)
        .lane(direction)
        .direction(lane)
        .build();
    List<TrafficInformationRequest> requests = Collections.singletonList(request);

    TrafficInformation trafficInformation = new TrafficInformation();
    trafficInformation.setVehicles(2);
    trafficInformation.setDevice(device);
    trafficInformation.setDirectionLane(directionLane);
    trafficInformation.setId(1L);
    trafficInformation.setCreatedDate(null);
    trafficInformation.setSaturation(10);
    trafficInformation.setSpeed(trafficInformation.getSaturation());

    trafficInformation.setCreatedDate(trafficInformation.getCreatedDate());
    trafficInformation.setPeriodDate(trafficInformation.getPeriodDate());
    trafficInformation.setId(trafficInformation.getId());
    trafficInformation.setVehicles(trafficInformation.getVehicles());
    trafficInformation.setDevice(trafficInformation.getDevice());
    trafficInformation.setDirectionLane(trafficInformation.getDirectionLane());
    trafficInformation.setSpeed(trafficInformation.getSpeed());
    List<TrafficInformation> trafficInformationList = Collections.singletonList(trafficInformation);

    Mockito.when(trafficInformationRepository.saveAll(Mockito.any())).thenReturn(trafficInformationList);

    List<TrafficInformation> createdTrafficInformationList = trafficInformationService.create(trafficInformation.getDevice(), directionLaneList, requests);

    Mockito.verify(trafficInformationRepository).saveAll(Mockito.any());
    Assert.assertThat(createdTrafficInformationList, Matchers.hasItem(trafficInformation));
  }

  private DirectionLane getDirectionLane() {
    DirectionLane directionLane = new DirectionLane();
    directionLane.setDirection(direction);
    directionLane.setLane(lane);
    return directionLane;
  }

  private Device getDevice() {
    Device device = new Device();
    device.setHighway(highway);
    device.setName(name);
    return device;
  }

  @Test
  public void testFilterIsNull() {
    ExportRequest  exportRequest = new ExportRequest();
    TrafficSearchRequest searchRequest = new TrafficSearchRequest();

    Sort sort = generateSort(searchRequest);
    searchRequest.setPage(0);
    searchRequest.setSize(10);
    Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
    Mockito.when(trafficInformationRepository.filterPeriodDate(searchRequest, pageable)).thenReturn(null);
    FilterResponse<TrafficMeasurementResponse> result = trafficInformationService.filter(searchRequest, true);
    Assert.assertNull(result.getData());
    searchRequest = TrafficSearchRequest.builder()
            .orders(exportRequest.getOrders())
            .toTimestamp(exportRequest.getToTimestamp())
            .fromTimestamp(exportRequest.getFromTimestamp())
            .highway(exportRequest.getHighway())
            .deviceNames(exportRequest.getDeviceNames())
            .build();
    Pageable pageableExport = PageRequest.of(0, Integer.MAX_VALUE, sort);
    Mockito.when(trafficInformationRepository.filterPeriodDate(searchRequest, pageableExport)).thenReturn(null);
    FilterResponse<TrafficMeasurementResponse> resultExport = trafficInformationService.filter(searchRequest, false);
    Assert.assertNull(resultExport.getData());
  }

  @Test
  public void testFilterNotNull(){
    MeasurementInformation information = new MeasurementInformation(
            "originId",
            "deviceName",
            "direction",
            "lane",
            10,
            10,
            10,
            new Timestamp(100L)
    );
    information.setDeviceName(information.getDeviceName());
    information.setDirection(information.getDirection());
    information.setLane(information.getLane());
    information.setOriginId(information.getOriginId());
    information.setPeriodDate(information.getPeriodDate());
    information.setSaturation(information.getSaturation());
    information.setSpeed(information.getSpeed());
    information.setVehicles(information.getVehicles());

    DirectionRequest directionRequest = new DirectionRequest();
    directionRequest.setName(null);
    directionRequest.setLane(null);
    directionRequest.setLane(directionRequest.getLane());
    directionRequest.setName(directionRequest.getName());
    Map<String, String> oder = new HashMap<>();
    oder.put("period_date","DESC");
    TrafficSearchRequest searchRequest = new TrafficSearchRequest();
    searchRequest.setSize(10);
    searchRequest.setPage(0);
    searchRequest.setOrders(oder);
    Sort sort = generateSort(searchRequest);
    Timestamp date = new Timestamp(1000000000L);
    Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
    Page<Timestamp> testTimestamp = new PageImpl<>(Collections.singletonList(date), pageable, searchRequest.getSize());
    Mockito.when(trafficInformationRepository.filterPeriodDate(searchRequest, pageable)).thenReturn(testTimestamp);
    FilterResponse<TrafficMeasurementResponse> result = trafficInformationService.filter(searchRequest, true);
    Assert.assertNotNull(result);
  }


  private Sort generateSort(TrafficSearchRequest searchRequest) {
    if (CollectionUtils.isEmpty(searchRequest.getOrders())) {
      Sort.Order order = new Sort.Order(Sort.Direction.DESC, "period_date");
      return Sort.by(Collections.singletonList(order));
    }
    List<Sort.Order> orders = new ArrayList<>();
    for ( Map.Entry<String, String> entry: searchRequest.getOrders().entrySet()) {
      Sort.Direction direction = Sort.Direction.fromString(entry.getValue());
      Sort.Order order = new Sort.Order(direction, entry.getKey());
      orders.add(order);
    }
    return Sort.by(orders);
  }

    @Test
    public void testTimestampNull() {
      ExportRequest exportRequest = new ExportRequest();
      exportRequest.setHighway(highway);
      exportRequest.setDeviceNames(Collections.singletonList(name));
      HttpServletResponse response = new MockHttpServletResponse();
      RestResult<Resource> result = trafficInformationService.exportCSV(exportRequest);
      Assert.assertNull(result.getData());
    }

}
