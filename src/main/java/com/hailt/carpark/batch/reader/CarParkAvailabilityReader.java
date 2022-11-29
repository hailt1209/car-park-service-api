package com.hailt.carpark.batch.reader;

import com.hailt.carpark.batch.dto.CarParkAvailabilityDTO;
import com.hailt.carpark.batch.dto.CarParkAvailabilityResponse;
import com.hailt.carpark.batch.dto.CarParkDataDTO;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarParkAvailabilityReader implements ItemReader<CarParkAvailabilityDTO> {

    private String carParkAvailabilityEndpoint;
    private final RestTemplate restTemplate;
    private int nextIndex;
    private List<CarParkAvailabilityDTO> carParkAvailabilityDTOs;

    public CarParkAvailabilityReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CarParkAvailabilityDTO read() {
        if (CollectionUtils.isEmpty(carParkAvailabilityDTOs)) {
            carParkAvailabilityDTOs = fetchFromAPI();
        }

        CarParkAvailabilityDTO nextCarParkAvailability = null;
        if (nextIndex < carParkAvailabilityDTOs.size()) {
            nextCarParkAvailability = carParkAvailabilityDTOs.get(nextIndex++);
        } else {
            nextIndex = 0;
            carParkAvailabilityDTOs = null;
        }

        return nextCarParkAvailability;
    }

    @Value("${car.park.availability.rest.endpoint}")
    public void setCarParkAvailabilityEndpoint(String carParkAvailabilityEndpoint) {
        this.carParkAvailabilityEndpoint = carParkAvailabilityEndpoint;
    }

    private List<CarParkAvailabilityDTO> fetchFromAPI() {
        ResponseEntity<CarParkAvailabilityResponse> response = restTemplate.getForEntity(carParkAvailabilityEndpoint, CarParkAvailabilityResponse.class);
        return convertToCarParkAvailabilityDTOs(response.getBody());
    }

    private List<CarParkAvailabilityDTO> convertToCarParkAvailabilityDTOs(CarParkAvailabilityResponse response) {
        if (response == null) {
            return new ArrayList<>();
        }
        List<CarParkDataDTO> carParkData = response.getItems().get(0).getCarParkData();
        return carParkData.stream().map(this::getCarParkAvailabilityDTO).collect(Collectors.toList());
    }

    private CarParkAvailabilityDTO getCarParkAvailabilityDTO(CarParkDataDTO carParkDataDTO) {
        return CarParkAvailabilityDTO.builder()
                .carParkNo(carParkDataDTO.getCarParkNumber())
                .totalLots(carParkDataDTO.getCarParkInfo().get(0).getTotalLots())
                .lotsAvailable(carParkDataDTO.getCarParkInfo().get(0).getLotsAvailable())
                .build();
    }
}
