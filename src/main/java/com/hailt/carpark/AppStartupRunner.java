package com.hailt.carpark;

import com.hailt.carpark.batch.component.CarParkLoader;
import com.hailt.carpark.service.CarParkService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class AppStartupRunner implements ApplicationRunner {

    private final CarParkService carParkService;
    private final CarParkLoader carParkLoader;

    public AppStartupRunner(CarParkService carParkService, CarParkLoader carParkLoader) {
        this.carParkService = carParkService;
        this.carParkLoader = carParkLoader;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (carParkService.countAllCarParks() == 0) {
            carParkLoader.loadData();
        }
    }
}
