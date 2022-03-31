package rmhub.mod.measurementtraffic.repository.custom;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;

public interface TrafficInformationRepositoryCustom {

  Page<Timestamp> filterPeriodDate(TrafficSearchRequest searchRequest, Pageable pageable);

  List<MeasurementInformation> findMeasurements(List<Timestamp> timestamps, List<String> directions, List<String> lanes, List<String> devices);


}
