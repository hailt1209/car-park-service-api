package com.hailt.carpark.repository;

import com.hailt.carpark.dto.AvailableCarParkDTO;
import com.hailt.carpark.entity.CarParkAvailability;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, Long> {

    @Query( countQuery = "SELECT count(cpa) " +
            "FROM CarParkAvailability cpa " +
            "WHERE cpa.lotsAvailable > 0 ",
            value = "SELECT new com.hailt.carpark.dto.AvailableCarParkDTO (" +
            "       cp.address, " +
            "       asText(cp.coordinate) as coordinateAsText, " +
            "       cpa.totalLots, " +
            "       cpa.lotsAvailable, " +
            "       distance(geography(cp.coordinate), geography(:coordinate)) as distance) " +
            "FROM CarParkAvailability cpa LEFT JOIN cpa.carPark cp  " +
            "WHERE cpa.lotsAvailable > 0 " +
            "ORDER BY distance")
    Page<AvailableCarParkDTO> findAvailableCarParksSortByDistance(@Param("coordinate") Point coordinate, Pageable pageable);


}
