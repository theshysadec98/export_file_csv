package rmhub.mod.measurementtraffic.service;

import rmhub.mod.measurementtraffic.model.kafka.consumer.Measurement;

public interface MeasurementTrafficService {

  void persistMeasurement(Measurement measurement);
}
