package com.hailt.carpark.rest.controller;

import com.hailt.carpark.batch.component.CarParkAvailabilityLoader;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/car-park-availability")
public class CarParkAvailabilityController {

    @Autowired
    private CarParkAvailabilityLoader carParkAvailabilityLoader;

    @GetMapping("/load")
    public ResponseEntity<String> availability() {
        JobExecution jobExecution = carParkAvailabilityLoader.loadData();
        return ResponseEntity.ok(jobExecution.getExitStatus().getExitCode());
    }
}
