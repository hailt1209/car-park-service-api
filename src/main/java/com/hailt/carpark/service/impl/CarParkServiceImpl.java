package com.hailt.carpark.service.impl;

import com.hailt.carpark.repository.CarParkRepository;
import com.hailt.carpark.service.CarParkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CarParkServiceImpl implements CarParkService {

    private final CarParkRepository carParkRepository;

    public CarParkServiceImpl(CarParkRepository carParkRepository) {
        this.carParkRepository = carParkRepository;
    }

    @Override
    public long countAllCarParks() {
        return carParkRepository.count();
    }
}
