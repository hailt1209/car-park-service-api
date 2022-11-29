package com.hailt.carpark.batch.listener;

import com.hailt.carpark.batch.component.CarParkAvailabilityLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CarParkLoadCompleteListener extends JobExecutionListenerSupport {

    private final CarParkAvailabilityLoader carParkAvailabilityLoader;

    public CarParkLoadCompleteListener(CarParkAvailabilityLoader carParkAvailabilityLoader) {
        this.carParkAvailabilityLoader = carParkAvailabilityLoader;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (ExitStatus.COMPLETED.getExitCode().equals(jobExecution.getExitStatus().getExitCode())) {
            log.info("Car park loader completed. Continue to load the availability.");
            carParkAvailabilityLoader.loadData();
        }
    }
}
