package com.hailt.carpark.batch.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hailt.carpark.batch.dto.CarParkAvailabilityDTO;
import com.hailt.carpark.batch.dto.CarParkAvailabilityResponse;
import com.hailt.carpark.helper.FileHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarParkAvailabilityReaderTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CarParkAvailabilityReader carParkAvailabilityReader;

    @Before
    public void setup() throws JsonProcessingException {
        String jsonString = FileHelper.readContent("/data/car-park-availability.json");
        ObjectMapper objectMapper = new ObjectMapper();
        CarParkAvailabilityResponse carParkAvailabilityResponse = objectMapper.readValue(jsonString, CarParkAvailabilityResponse.class);
        ResponseEntity<CarParkAvailabilityResponse> responseEntity = ResponseEntity.ok(carParkAvailabilityResponse);
        String dumpEndpoint = "dumpEndpoint";

        when(restTemplate.getForEntity(eq(dumpEndpoint), eq(CarParkAvailabilityResponse.class))).thenReturn(responseEntity);
        carParkAvailabilityReader.setCarParkAvailabilityEndpoint(dumpEndpoint);
    }

    @After
    public void teardown() {
        reset(restTemplate);
    }

    @Test
    public void read_theFirstTime_shouldLoadDataFromRestEndpoint() {

        CarParkAvailabilityDTO availabilityDTO = carParkAvailabilityReader.read();

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CarParkAvailabilityResponse.class));
        assertThat(availabilityDTO.getCarParkNo()).isEqualTo("HE12");
    }

    @Test
    public void readTwoTimes_shouldReturn2ndItem() {


        CarParkAvailabilityDTO availabilityDTO = readMany(2);

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CarParkAvailabilityResponse.class));
        assertThat(availabilityDTO.getCarParkNo()).isEqualTo("HLM");
    }

    @Test
    public void readFiveTimes_shouldReturnEmpty() {

        CarParkAvailabilityDTO availabilityDTO = readMany(5);

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CarParkAvailabilityResponse.class));
        assertThat(availabilityDTO).isNull();
    }

    private CarParkAvailabilityDTO readMany(int time) {
        CarParkAvailabilityDTO availabilityDTO = null;
        for (int i = 0; i < time; i++) {
            availabilityDTO = carParkAvailabilityReader.read();
        }
        return availabilityDTO;
    }
}
