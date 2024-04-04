package rmhub.mod.measurementtraffic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.request.DeviceRequest;
import rmhub.mod.measurementtraffic.repository.DeviceRepository;
import rmhub.mod.measurementtraffic.service.DeviceService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {


  private final DeviceRepository repository;

  @Override
  public Device getOrCreate(DeviceRequest deviceRequest) {
    if (StringUtils.isEmpty(deviceRequest.getHighway()) || StringUtils.isEmpty(deviceRequest.getName())) {
      log.info("Do not create device, highway and name must be not null");
      return new Device();
    }
    Device device = repository.findFirstByHighwayAndName(deviceRequest.getHighway(), deviceRequest.getName());
    if (device != null) {
      if( StringUtils.isEmpty(device.getOriginId()) ){
        device.setOriginId(device.getName());
      }
      repository.save(device);
      return device;
    }
    device = new Device();
    device.setHighway(deviceRequest.getHighway());
    device.setOriginId(deviceRequest.getName());
    device.setName(deviceRequest.getName());
    return repository.save(device);
  }
}
