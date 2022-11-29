package com.hailt.carpark.repository;

import com.hailt.carpark.entity.CarPark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarParkRepository extends JpaRepository<CarPark, Long> {
}
