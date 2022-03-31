package rmhub.mod.measurementtraffic.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import rmhub.mod.measurementtraffic.model.entity.Device;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;
import rmhub.mod.measurementtraffic.model.entity.MeasurementInformation;
import rmhub.mod.measurementtraffic.model.entity.TrafficInformation;
import rmhub.mod.measurementtraffic.model.TrafficSearchRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
public class TrafficInformationRepositoryCustomTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrafficInformationRepository repository;

    private String highway;

    private String originId;

    @Before
    public void setUp() {
        highway = "M44";
        originId = "DV_1";
        Device device = getPersistingDevice(highway, originId);
        DirectionLane directionLane = getPersistingDirectionLane();
        persist(device, directionLane);
        List<TrafficInformation> trafficInformationList = getPersistingTraffic(device, directionLane);
        persistWeatherInformationList(trafficInformationList);
    }

    @Test
    public void testFilterPeriodDateNoData() {
        TrafficSearchRequest request = new TrafficSearchRequest();
        request.setHighway("M45");
        Page<Timestamp> timestamps = repository.filterPeriodDate(request, PageRequest.of(0, 10));
        Assertions.assertEquals(0, timestamps.getTotalElements(), "Unexpected exit code");
    }

    @Test
    public void testFilterPeriodDateHasData() {
        TrafficSearchRequest request = new TrafficSearchRequest();
        request.setHighway("M44");
        request.setDeviceNames(Collections.singletonList("DV_1"));
        Page<Timestamp> timestampPage = repository.filterPeriodDate(request, PageRequest.of(0, 10));
        Assert.assertEquals(11, timestampPage.getTotalElements());
        Assert.assertEquals(2, timestampPage.getTotalPages());
    }

    @Test
    public void testFindMeasurementsNoData(){
        Timestamp timestamp = new Timestamp(1000000L);
        String direction = "2";
        String lane = "2";
        String deviceName = "name";

        List<MeasurementInformation> informationList = repository.findMeasurements(Collections.singletonList(timestamp),
                Collections.singletonList(direction),
                Collections.singletonList(lane),
                Collections.singletonList(deviceName));
        Assert.assertEquals(0, informationList.size());
    }


    private List<TrafficInformation> getPersistingTraffic(Device device, DirectionLane directionLane){
        List<TrafficInformation> informationList = new ArrayList<>();
        long startTime = 1640912400000L;
        for ( int index = 0; index < 11; index++ ) {
            Timestamp timestamp = new Timestamp(startTime + index*60000L);
            TrafficInformation information = new TrafficInformation();
            information.setDevice(device);
            information.setPeriodDate(timestamp);
            information.setCreatedDate(timestamp);
            information.setSaturation(1+index);
            information.setSpeed(1+index);
            information.setVehicles(1+index);
            information.setDirectionLane(directionLane);
            informationList.add(information);
        }
        return informationList;
    }

    private void persistWeatherInformationList(List<TrafficInformation> informationList) {
        informationList.forEach(trafficInformation -> {
            entityManager.persist(trafficInformation);
            entityManager.flush();
            entityManager.clear();
        });
    }

    private void persist(Device device, DirectionLane directionLane) {
        entityManager.persist(device);
        entityManager.persist(directionLane);
        entityManager.flush();
    }


    private DirectionLane getPersistingDirectionLane(){
        DirectionLane directionLane = new DirectionLane();
        directionLane.setDirection("1");
        directionLane.setLane("1");
        return directionLane;
    }

    private Device getPersistingDevice(String highway, String originId) {
        Device device = new Device();
        device.setHighway(highway);
        device.setName("DV_1");
        device.setOriginId(originId);
        return device;
    }
}