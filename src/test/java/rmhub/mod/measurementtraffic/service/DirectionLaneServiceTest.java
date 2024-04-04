package rmhub.mod.measurementtraffic.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.request.DirectionLaneRequest;
import rmhub.mod.measurementtraffic.repository.DirectionLaneRepository;
import rmhub.mod.measurementtraffic.service.impl.DirectionLaneServiceImpl;

@SpringBootTest
public class DirectionLaneServiceTest {

  @Autowired
  private DirectionLaneService directionLaneService;

  @Mock
  private DirectionLaneRepository directionLaneRepository;

  private String createdDirection;
  private String createdLane;

  private String creatingDirection;
  private String creatingLane;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    this.createdDirection = "1";
    this.createdLane = "1";
    this.creatingDirection = "2";
    this.creatingLane = "2";
  }

  @Test
  public void testGetOrCreate() {
    DirectionLane createdDirectionLane = new DirectionLane();
    createdDirectionLane.setDirection(createdDirection);
    createdDirectionLane.setLane(createdLane);

    DirectionLane creatingDirectionLane = new DirectionLane();
    creatingDirectionLane.setDirection(creatingDirection);
    creatingDirectionLane.setLane(creatingLane);

    DirectionLaneRequest persistedRequest = DirectionLaneRequest.builder()
        .direction(createdDirection)
        .lane(createdLane)
        .build();
    persistedRequest.setLane(createdLane);
    DirectionLaneRequest noPersistedRequest = DirectionLaneRequest.builder()
        .direction(creatingDirection)
        .lane(creatingLane)
        .build();
    List<DirectionLaneRequest> requests = Arrays.asList(persistedRequest, noPersistedRequest);

    Mockito.when(directionLaneRepository.findFirstByDirectionAndLane(Mockito.eq(createdDirection), Mockito.eq(createdLane))).thenReturn(createdDirectionLane);
    Mockito.when(directionLaneRepository.findFirstByDirectionAndLane(Mockito.eq(creatingDirection), Mockito.eq(creatingLane))).thenReturn(null);
    Mockito.when(directionLaneRepository.saveAll(Mockito.any())).thenReturn(Collections.singletonList(creatingDirectionLane));

    List<DirectionLane> directionLaneList = this.directionLaneService.getOrCreate(requests);

    Mockito.verify(directionLaneRepository, Mockito.times(2)).findFirstByDirectionAndLane(Mockito.anyString(), Mockito.anyString());
    Mockito.verify(directionLaneRepository).saveAll(Mockito.any());
    Assert.assertThat(directionLaneList, Matchers.hasItem(createdDirectionLane));
    Assert.assertThat(directionLaneList, Matchers.hasItem(creatingDirectionLane));
  }
}
