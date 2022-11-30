package com.hailt.carpark.helper;

import com.hailt.carpark.entity.CarPark;
import com.hailt.carpark.entity.CarParkAvailability;
import com.hailt.carpark.repository.CarParkAvailabilityRepository;
import com.hailt.carpark.repository.CarParkRepository;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.EntityManager;
import java.util.Date;

public class DatabaseHelper {

    public static CarPark createCarPark(CarParkRepository carParkRepository, String carParkNo, double latitude, double longitude) {
        CarPark carPark = CarPark.builder()
                .carParkNo(carParkNo)
                .address(RandomStringUtils.randomAlphabetic(100))
                .coordinate(GeometryUtil.convertCoordinateToPoint(latitude, longitude))
                .build();
        return carParkRepository.saveAndFlush(carPark);
    }

    public static CarPark createCarPark(EntityManager entityManager, String carParkNo, double latitude, double longitude) {
        CarPark carPark = CarPark.builder()
                .carParkNo(carParkNo)
                .address(RandomStringUtils.randomAlphabetic(100))
                .coordinate(GeometryUtil.convertCoordinateToPoint(latitude, longitude))
                .build();
        entityManager.persist(carPark);
        entityManager.flush();
        return carPark;
    }

    public static CarParkAvailability createCarPackAvailability(CarParkAvailabilityRepository carParkAvailabilityRepository, CarPark carPark, Integer totalLots, Integer lotsAvailable) {
        CarParkAvailability carParkAvailability = CarParkAvailability.builder()
                .carPark(carPark)
                .lotsAvailable(lotsAvailable)
                .totalLots(totalLots)
                .lastUpdatedOn(new Date())
                .build();

        return carParkAvailabilityRepository.saveAndFlush(carParkAvailability);
    }

    public static CarParkAvailability createCarPackAvailability(EntityManager entityManager, CarPark carPark, Integer totalLots, Integer lotsAvailable) {
        CarParkAvailability carParkAvailability = CarParkAvailability.builder()
                .carPark(carPark)
                .lotsAvailable(lotsAvailable)
                .totalLots(totalLots)
                .lastUpdatedOn(new Date())
                .build();

        entityManager.persist(carParkAvailability);
        entityManager.flush();
        return carParkAvailability;
    }
}
