package rmhub.mod.measurementtraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;

public interface DirectionLaneRepository extends JpaRepository<DirectionLane, Integer> {

  DirectionLane findFirstByDirectionAndLane(String direction, String lane);
}
