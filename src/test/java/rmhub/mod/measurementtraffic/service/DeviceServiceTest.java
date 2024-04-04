package rmhub.mod.measurementtraffic.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.request.DeviceRequest;
import rmhub.mod.measurementtraffic.repository.DeviceRepository;
import rmhub.mod.measurementtraffic.service.impl.DeviceServiceImpl;

@SpringBootTest
public class DeviceServiceTest {

  private DeviceService deviceService;

  @Mock
  private DeviceRepository deviceRepository;

  private String highway;

  private String name;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    deviceService = new DeviceServiceImpl(deviceRepository);
    highway = "M33";
    name = "DC_32_12";
  }

  @Test
  public void testGetDevice() {
    DeviceRequest deviceRequest = getRequest();
    deviceRequest.setName(name);
    Device device = getDevice();

    Mockito.when(deviceRepository.findFirstByHighwayAndName(highway, name)).thenReturn(device);

    Device entity = deviceService.getOrCreate(deviceRequest);

    Mockito.verify(deviceRepository).findFirstByHighwayAndName(highway, name);
    Assert.assertEquals(entity.getHighway(), highway);
    Assert.assertEquals(entity.getName(), name);
  }

  @Test
  public void testGetDeviceWithRequestNull() {
    DeviceRequest deviceRequest = new DeviceRequest();
    Device entity = this.deviceService.getOrCreate(deviceRequest);
    Assert.assertNull(entity.getHighway());
    Assert.assertNull(entity.getName());
  }

  @Test
  public void testCreateDevice() {
    DeviceRequest deviceRequest = getRequest();
    Device device = getDevice();

    Mockito.when(deviceRepository.findFirstByHighwayAndName(highway, name)).thenReturn(null);
    Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(device);

    Device entity = deviceService.getOrCreate(deviceRequest);

    Mockito.verify(deviceRepository).findFirstByHighwayAndName(highway, name);
    Mockito.verify(deviceRepository).save(Mockito.any());
    Assert.assertEquals(entity.getId(), device.getId());
    Assert.assertEquals(entity.getHighway(), highway);
    Assert.assertEquals(entity.getName(), name);
  }

  private Device getDevice() {
    Device device = new Device();
    device.setHighway(highway);
    device.setName(name);
    return device;
  }

  private DeviceRequest getRequest() {
    return DeviceRequest.builder()
        .highway(highway)
        .name(name)
        .build();
  }
}
