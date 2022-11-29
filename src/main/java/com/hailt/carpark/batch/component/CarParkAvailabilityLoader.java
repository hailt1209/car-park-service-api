package com.hailt.carpark.batch.component;

import com.hailt.carpark.batch.dto.CarParkAvailabilityDTO;
import com.hailt.carpark.batch.reader.CarParkAvailabilityReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CarParkAvailabilityLoader {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CarParkAvailabilityReader carParkAvailabilityItemReader;
    private final ItemWriter<CarParkAvailabilityDTO> carParkAvailabilityItemWriter;

    public CarParkAvailabilityLoader(JobLauncher jobLauncher, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, CarParkAvailabilityReader carParkAvailabilityItemReader, ItemWriter<CarParkAvailabilityDTO> carParkAvailabilityItemWriter) {
        this.jobLauncher = jobLauncher;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.carParkAvailabilityItemReader = carParkAvailabilityItemReader;
        this.carParkAvailabilityItemWriter = carParkAvailabilityItemWriter;
    }

    public JobExecution loadData() {
        log.info("Start loading car park availability data from REST API.");
        try {
            Map<String, JobParameter> parameters = new HashMap<>();
            parameters.put("launching-time", new JobParameter(new Date()));
            parameters.put("name", new JobParameter(this.getClass().getName()));
            return jobLauncher.run(buildJob(), new JobParameters(parameters));
        } catch (Exception e) {
            log.error("Failed to load car park availability data from REST API. {}", e.getMessage(), e);
        }
        return null;
    }

    private Job buildJob() {
        return jobBuilderFactory.get("loadCarParkAvailabilityData")
                .incrementer(new RunIdIncrementer())
                .flow(fromRestToDatabaseStep())
                .end()
                .build();
    }

    private Step fromRestToDatabaseStep() {
        return stepBuilderFactory.get("loadCarParkAvailabilityStep")
                .<CarParkAvailabilityDTO, CarParkAvailabilityDTO>chunk(100)
                .reader(carParkAvailabilityItemReader)
                .writer(carParkAvailabilityItemWriter)
                .build();
    }
}
