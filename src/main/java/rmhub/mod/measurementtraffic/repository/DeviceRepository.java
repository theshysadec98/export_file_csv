package rmhub.mod.measurementtraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rmhub.mod.measurementtraffic.model.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

  Device findFirstByHighwayAndName(String highway, String name);
}
