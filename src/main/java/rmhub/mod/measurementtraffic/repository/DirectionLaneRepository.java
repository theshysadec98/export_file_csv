package rmhub.mod.measurementtraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rmhub.mod.measurementtraffic.model.entity.DirectionLane;

@Repository
public interface DirectionLaneRepository extends JpaRepository<DirectionLane, Integer> {

  DirectionLane findFirstByDirectionAndLane(String direction, String lane);
}
