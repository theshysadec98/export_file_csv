package rmhub.mod.measurementtraffic.controller;

import java.util.Collections;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmhub.mod.measurementtraffic.model.ExportRequest;
import rmhub.mod.measurementtraffic.model.ResultListTrafficMeasurementResponse;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;
import rmhub.mod.measurementtraffic.model.response.FilterResponse;
import rmhub.mod.measurementtraffic.model.response.RestResult;
import rmhub.mod.measurementtraffic.model.TrafficMeasurementResponse;
import rmhub.mod.measurementtraffic.service.TrafficInformationService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrafficController implements TrafficApi{

  private final TrafficInformationService trafficInformationService;

  @Override
  public ResponseEntity<ResultListTrafficMeasurementResponse> filter(TrafficSearchRequest trafficSearchRequest) {
    FilterResponse<TrafficMeasurementResponse> result = trafficInformationService.filter(trafficSearchRequest, true);
    return new ResponseEntity<>(
            ResultListTrafficMeasurementResponse.builder()
                    .data(result.getData())
                    .metadata(result.paginationMap())
                    .message(Collections.singletonList(RestResult.STATUS_SUCCESS))
                    .timestamp(new Date().getTime())
                    .build(),
            HttpStatus.OK
    );
  }

  @Override
  public ResponseEntity<Object> export(ExportRequest exportRequest) {
    RestResult<Resource> dataResponse = trafficInformationService.exportCSV(exportRequest);
    if(dataResponse.getData() == null)
      return ResponseEntity.ok(RestResult.builder()
              .messages(dataResponse.getMessages())
              .status(dataResponse.getStatus())
              .build());
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            .header("Content-Disposition","attachment; filename=TrafficLogger.csv")
            .body(dataResponse.getData());
  }
}
