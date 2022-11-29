package com.hailt.carpark.service.impl;

import com.hailt.carpark.repository.CarParkAvailabilityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CarParkAvailabilityServiceImplTest {

    @Mock
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @InjectMocks
    private CarParkAvailabilityServiceImpl carParkAvailabilityService;

    @Test
    public void getNearestAvailableCarParks_shouldInvokeRepository() {
        Pageable pageable = Pageable.unpaged();

        carParkAvailabilityService.getNearestAvailableCarParks(12.2, 23.4, pageable);

        verify(carParkAvailabilityRepository, times(1)).findAvailableCarParksSortByDistance(any(Point.class), eq(pageable));
    }
}
