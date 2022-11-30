package com.hailt.carpark.service;

import com.hailt.carpark.dto.AvailableCarParkDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarParkAvailabilityService {
    Page<AvailableCarParkDTO> getNearestAvailableCarParks(Double latitude, Double longitude, Pageable pageable);
}
