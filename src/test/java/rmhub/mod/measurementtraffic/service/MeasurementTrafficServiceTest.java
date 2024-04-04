package rmhub.mod.measurementtraffic.service;

import java.sql.Timestamp;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.kafka.consumer.*;
import rmhub.mod.measurementtraffic.service.impl.MeasurementTrafficServiceImpl;

@SpringBootTest
public class MeasurementTrafficServiceTest {

  private MeasurementTrafficService measurementTrafficService;

  @Mock
  private DeviceService deviceService;

  @Mock
  private DirectionLaneService directionLaneService;

  @Mock
  private TrafficInformationService trafficInformationService;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    measurementTrafficService = new MeasurementTrafficServiceImpl(deviceService, directionLaneService, trafficInformationService);
  }

  @Test
  public void testPersistNoMeasurement() {
    measurementTrafficService.persistMeasurement(null);

    Mockito.verify(deviceService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(directionLaneService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(trafficInformationService, Mockito.never()).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testPersistNoHighwayOfMeasurement() {
    Measurement measurement = new Measurement();
    measurement.setName("RH_12_32");

    measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(deviceService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(directionLaneService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(trafficInformationService, Mockito.never()).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testPersistNoNameOfMeasurement() {
    Measurement measurement = new Measurement();
    measurement.setHighway("M32");

    measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(deviceService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(directionLaneService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(trafficInformationService, Mockito.never()).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testPersistNoNameOfMeasurementWithRequestNull() {
    Measurement measurement = new Measurement();

    measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(deviceService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(directionLaneService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(trafficInformationService, Mockito.never()).create(Mockito.any(), Mockito.any(), Mockito.any());
  }
  @Test
  public void testPersistNoCreatedDevice() {
    Measurement measurement = new Measurement();
    measurement.setHighway("M32");
    measurement.setName("RH_12_32");

    Mockito.when(deviceService.getOrCreate(Mockito.any())).thenReturn(null);

    measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(directionLaneService, Mockito.never()).getOrCreate(Mockito.any());
    Mockito.verify(trafficInformationService, Mockito.never()).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testPersistNoDirectionLane() {
    Measurement measurement = new Measurement();
    measurement.setTimestamp(new Timestamp(1000L));
    measurement.setTimestamp(measurement.getTimestamp());
    measurement.setHighway("M32");
    measurement.setName("RH_12_32");
    Speed speed = new Speed();
    speed.setDirection("1");
    speed.setLane("1");
    speed.setValue(1);
    speed.setAdditionalProperty("name", null);
    speed.setAdditionalProperty("name", speed.getAdditionalProperties());
    Saturation saturation = new Saturation();
    saturation.setValue(1);
    saturation.setLane("lane");
    saturation.setDirection("direction");
    saturation.setAdditionalProperty("name", null);
    saturation.setAdditionalProperty("name", saturation.getAdditionalProperties());
    Vehicle vehicle = new Vehicle();
    vehicle.setValue(1);
    vehicle.setLane("lane");
    vehicle.setDirection("direction");
    vehicle.setAdditionalProperty("name", null);
    vehicle.setAdditionalProperty("name", vehicle.getAdditionalProperties());

    measurement.setData(null);

    DirectionLane directionLane = new DirectionLane();

    Mockito.when(this.deviceService.getOrCreate(Mockito.any())).thenReturn(new Device());
    Mockito.when(this.directionLaneService.getOrCreate(Mockito.any())).thenReturn(Collections.singletonList(directionLane));

    this.measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(trafficInformationService).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testPersistSuccess() {
    Measurement measurement = new Measurement();
    measurement.setTimestamp(new Timestamp(1000L));
    measurement.setTimestamp(measurement.getTimestamp());
    measurement.setHighway("M32");
    measurement.setName("RH_12_32");
    Speed speed = new Speed();
    speed.setDirection("1");
    speed.setLane("1");
    speed.setValue(1);
    speed.setAdditionalProperty("name", null);
    speed.setAdditionalProperty("name", speed.getAdditionalProperties());
    Saturation saturation = new Saturation();
    saturation.setValue(1);
    saturation.setLane("lane");
    saturation.setDirection("direction");
    saturation.setAdditionalProperty("name", null);
    saturation.setAdditionalProperty("name", saturation.getAdditionalProperties());
    Vehicle vehicle = new Vehicle();
    vehicle.setValue(1);
    vehicle.setLane("lane");
    vehicle.setDirection("direction");
    vehicle.setAdditionalProperty("name", null);
    vehicle.setAdditionalProperty("name", vehicle.getAdditionalProperties());
    Data data = new Data();
    data.setSpeed(Collections.singletonList(speed));
    data.setSaturation(Collections.singletonList(saturation));
    data.setVehicles(Collections.singletonList(vehicle));
    measurement.setData(data);

    DirectionLane directionLane = new DirectionLane();

    Mockito.when(deviceService.getOrCreate(Mockito.any())).thenReturn(new Device());
    Mockito.when(directionLaneService.getOrCreate(Mockito.any())).thenReturn(Collections.singletonList(directionLane));

    measurementTrafficService.persistMeasurement(measurement);

    Mockito.verify(trafficInformationService).create(Mockito.any(), Mockito.any(), Mockito.any());
  }

}
