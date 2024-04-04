package rmhub.mod.measurementtraffic.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import rmhub.mod.measurementtraffic.model.kafka.consumer.Measurement;
import rmhub.mod.measurementtraffic.service.MeasurementTrafficService;

@Service
@ConditionalOnProperty(value = "kafka.enable", havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class MeasurementTrafficConsumerService {


  private final MeasurementTrafficService measurementTrafficService;

  private final ObjectMapper objectMapper;

  @KafkaListener(topics = {"${kafka.measurement.topic}"})
  public void listenTrafficMeasurement(String message, Acknowledgment acknowledgment) {
    log.info("Message of Kafka : {}", message);
    try {
      Measurement measurement = objectMapper.readValue(message, Measurement.class);
      measurementTrafficService.persistMeasurement(measurement);
      acknowledgment.acknowledge();
    } catch (JsonMappingException ex) {
      log.error("Json Mapping Exception: {}", ex.getMessage());
    } catch (JsonProcessingException ex) {
      log.error("Json Processing Exception: {}", ex.getMessage());
    }
  }


}
