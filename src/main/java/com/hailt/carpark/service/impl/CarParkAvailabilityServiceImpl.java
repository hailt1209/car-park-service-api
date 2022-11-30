package com.hailt.carpark.service.impl;

import com.hailt.carpark.dto.AvailableCarParkDTO;
import com.hailt.carpark.helper.GeometryUtil;
import com.hailt.carpark.repository.CarParkAvailabilityRepository;
import com.hailt.carpark.service.CarParkAvailabilityService;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CarParkAvailabilityServiceImpl implements CarParkAvailabilityService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;

    public CarParkAvailabilityServiceImpl(CarParkAvailabilityRepository carParkAvailabilityRepository) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
    }

    @Override
    public Page<AvailableCarParkDTO> getNearestAvailableCarParks(Double latitude, Double longitude, Pageable pageable) {
        Point point = GeometryUtil.convertCoordinateToPoint(latitude, longitude);
        return carParkAvailabilityRepository.findAvailableCarParksSortByDistance(point, pageable);
    }
}
