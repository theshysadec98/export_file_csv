package rmhub.mod.measurementtraffic.service;

import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.request.DeviceRequest;



public interface DeviceService {

  Device getOrCreate(DeviceRequest request);
}
