package com.hailt.carpark.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hailt.carpark.dto.RestResponsePage;
import com.hailt.carpark.entity.CarPark;
import com.hailt.carpark.entity.CarParkAvailability;
import com.hailt.carpark.helper.DatabaseHelper;
import com.hailt.carpark.repository.CarParkAvailabilityRepository;
import com.hailt.carpark.repository.CarParkRepository;
import com.hailt.carpark.rest.response.CarParkSearchResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarParkControllerIntegrationTest {

    @Autowired
    private CarParkRepository carParkRepository;

    @Autowired
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CarParkAvailability carPackAvailability1;
    private CarParkAvailability carPackAvailability2;
    private CarParkAvailability carPackAvailability3;
    private CarParkAvailability carPackAvailability4;


    @Before
    public void setup() {

        CarPark carPark1 = DatabaseHelper.createCarPark(carParkRepository, "HE12", 1.37, 103.89);
        CarPark carPark2 = DatabaseHelper.createCarPark(carParkRepository, "HLM", 12.74, 109.19);
        CarPark carPark3 = DatabaseHelper.createCarPark(carParkRepository, "RHM", 21.32, 84.11);
        CarPark carPark4 = DatabaseHelper.createCarPark(carParkRepository, "BM29", 4.56, 24.145);

        carPackAvailability1 = DatabaseHelper.createCarPackAvailability(carParkAvailabilityRepository, carPark1, 120, 30);
        carPackAvailability2 = DatabaseHelper.createCarPackAvailability(carParkAvailabilityRepository, carPark2, 77, 0);
        carPackAvailability3 = DatabaseHelper.createCarPackAvailability(carParkAvailabilityRepository, carPark3, 56, 3);
        carPackAvailability4 = DatabaseHelper.createCarPackAvailability(carParkAvailabilityRepository, carPark4, 16, 10);
    }

    @After
    public void teardown() {
        carParkAvailabilityRepository.deleteAll();
        carParkRepository.deleteAll();
    }

    @Test
    public void getNearestCarParks_latitudeIsMissing_returnBadRequest() throws Exception {
        sendGetRequest("/carparks/nearest")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].key", is("request.param.latitude.missing")))
                .andExpect(jsonPath("$.errors[0].message", is("Required request parameter 'latitude' for method parameter type Double is not present")));
    }

    @Test
    public void getNearestCarParks_longitudeIsMissing_returnBadRequest() throws Exception {
        sendGetRequest("/carparks/nearest?latitude=1.37326")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].key", is("request.param.longitude.missing")))
                .andExpect(jsonPath("$.errors[0].message", is("Required request parameter 'longitude' for method parameter type Double is not present")));
    }

    @Test
    public void getNearestCarParks_pageInvalid_returnBadRequest() throws Exception {
        sendGetRequest("/carparks/nearest?latitude=1.37326&longitude=103.897&page=-1")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].key", is("request.param.page.invalid")))
                .andExpect(jsonPath("$.errors[0].message", is("Request parameter 'page' should be greater than or equal to 0")));
    }

    @Test
    public void getNearestCarParks_perPageInvalid_returnBadRequest() throws Exception {
        sendGetRequest("/carparks/nearest?latitude=1.37326&longitude=103.897&per_page=0")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].key", is("request.param.perPage.invalid")))
                .andExpect(jsonPath("$.errors[0].message", is("Request parameter 'per_page' should be greater than or equal to 1")));
    }

    @Test
    public void getNearestCarParks_hasData_returnNearestOnTop() throws Exception {
        MvcResult mvcResult = sendGetRequest("/carparks/nearest?latitude=1.37326&longitude=103.897&page=0&per_page=3")
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<CarParkSearchResponse> responsePage = getCarParkSearchResponses(mvcResult);

        assertThat(responsePage.getTotalElements()).isEqualTo(3);
        assertMatchedValue(responsePage.getContent().get(0), carPackAvailability1);
        assertMatchedValue(responsePage.getContent().get(1), carPackAvailability3);
        assertMatchedValue(responsePage.getContent().get(2), carPackAvailability4);
    }

    @Test
    public void getNearestCarParks_hasPagination_returnSubset() throws Exception {
        MvcResult mvcResult = sendGetRequest("/carparks/nearest?latitude=1.37326&longitude=103.897&page=1&per_page=2")
                .andExpect(status().isOk()).andReturn();

        RestResponsePage<CarParkSearchResponse> responsePage = getCarParkSearchResponses(mvcResult);

        assertThat(responsePage.getTotalElements()).isEqualTo(3);
        assertThat(responsePage.getTotalPages()).isEqualTo(2);
        assertThat(responsePage.getNumberOfElements()).isEqualTo(1);
        assertMatchedValue(responsePage.getContent().get(0), carPackAvailability4);
    }

    private void assertMatchedValue(CarParkSearchResponse searchResponse, CarParkAvailability carParkAvailability) {
        assertThat(searchResponse.getAddress()).isEqualTo(carParkAvailability.getCarPark().getAddress());
        assertThat(searchResponse.getLatitude()).isEqualTo(carParkAvailability.getCarPark().getCoordinate().getY());
        assertThat(searchResponse.getLongitude()).isEqualTo(carParkAvailability.getCarPark().getCoordinate().getX());
        assertThat(searchResponse.getTotalLots()).isEqualTo(carParkAvailability.getTotalLots());
        assertThat(searchResponse.getLotsAvailable()).isEqualTo(carParkAvailability.getLotsAvailable());
    }

    private RestResponsePage<CarParkSearchResponse> getCarParkSearchResponses(MvcResult mvcResult) throws Exception {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() { });
    }

    private ResultActions sendGetRequest(String urlTemplate, Object... uriVars) throws Exception {
        return mockMvc.perform(get(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}
