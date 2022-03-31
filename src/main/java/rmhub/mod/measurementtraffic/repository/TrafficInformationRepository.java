package rmhub.mod.measurementtraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.entity.TrafficInformation;
import rmhub.mod.measurementtraffic.repository.custom.TrafficInformationRepositoryCustom;

import java.util.List;

public interface TrafficInformationRepository extends JpaRepository<TrafficInformation, Long>, TrafficInformationRepositoryCustom {
    @Query(" SELECT new rmhub.mod.measurementtraffic.model.entity.MeasurementInformation(d.originId ,d.name, dl.direction, dl.lane, o.saturation, o.speed, o.vehicles, o.periodDate) "
            + " FROM TrafficInformation o JOIN o.device d JOIN o.directionLane dl ")
    List<MeasurementInformation> findAllMeasurementTraffic();
}
