package rmhub.mod.measurementtraffic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import rmhub.mod.measurementtraffic.model.*;
import rmhub.mod.measurementtraffic.model.response.FilterResponse;
import rmhub.mod.measurementtraffic.model.response.RestResult;
import rmhub.mod.measurementtraffic.service.TrafficInformationService;

@RunWith(SpringRunner.class)
@WebMvcTest(TrafficController.class)
public class TrafficControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private TrafficInformationService trafficInformationService;

  @Test
  public void testNoTrafficFilter() throws Exception {
    Mockito.when(trafficInformationService.filter(Mockito.any(), Mockito.anyBoolean()))
        .thenReturn(FilterResponse.<TrafficMeasurementResponse>builder()
        .data(null)
        .build());
    TrafficSearchRequest searchRequest = new TrafficSearchRequest();
    searchRequest.setPage(0);
    searchRequest.setSize(10);

    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/traffic/filter")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(searchRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andReturn()
        .getResponse();
    ResultListTrafficMeasurementResponse result = objectMapper.readValue(response.getContentAsString(), ResultListTrafficMeasurementResponse.class);
    Assertions.assertThat(result.getData()).isNull();
  }

  @Test
  public void testTrafficFilter() throws Exception {
    TrafficSearchRequest searchRequest = new TrafficSearchRequest();
    searchRequest.setPage(0);
    searchRequest.setSize(10);

    LaneMeasurementResponse laneMeasurementResponse = LaneMeasurementResponse.builder()
        .name("1")
        .saturation(1)
        .speed(2)
        .vehicles(3)
        .build();
    DirectionMeasurementResponse directionMeasurementResponse = DirectionMeasurementResponse.builder()
        .name("0")
        .lanes(Collections.singletonList(laneMeasurementResponse))
        .build();
    DeviceMeasurementResponse deviceMeasurementResponse = DeviceMeasurementResponse.builder()
        .name("A1_23_54")
        .directions(Collections.singletonList(directionMeasurementResponse))
        .build();
    Date currentTime = new Date();
    TrafficMeasurementResponse trafficMeasurementResponse = TrafficMeasurementResponse.builder()
        .timestamp(currentTime.getTime())
        .deviceMeasurements(Collections.singletonList(deviceMeasurementResponse))
        .build();
    Mockito.when(trafficInformationService.filter(Mockito.any(), Mockito.anyBoolean()))
        .thenReturn(FilterResponse.<TrafficMeasurementResponse>builder()
            .data(Collections.singletonList(trafficMeasurementResponse))
            .page(0)
            .size(10)
            .build());

    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/traffic/filter")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(searchRequest))
        .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andReturn()
        .getResponse();
    ResultListTrafficMeasurementResponse result = objectMapper.readValue(response.getContentAsString(), ResultListTrafficMeasurementResponse.class);
    result = objectMapper.readValue(response.getContentAsString(), ResultListTrafficMeasurementResponse.class);
    Assert.assertEquals(1, result.getData().size());
  }

}
