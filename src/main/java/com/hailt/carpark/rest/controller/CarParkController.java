package com.hailt.carpark.rest.controller;

import com.hailt.carpark.dto.AvailableCarParkDTO;
import com.hailt.carpark.exception.InvalidInputException;
import com.hailt.carpark.rest.response.CarParkSearchResponse;
import com.hailt.carpark.service.CarParkAvailabilityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/carparks")
public class CarParkController {

    private final CarParkAvailabilityService carParkAvailabilityService;

    public CarParkController(CarParkAvailabilityService carParkAvailabilityService) {
        this.carParkAvailabilityService = carParkAvailabilityService;
    }

    @GetMapping("/nearest")
    public Page<CarParkSearchResponse> getNearestAvailableCarParks(@RequestParam("latitude") Double latitude,
                                                                   @RequestParam("longitude") Double longitude,
                                                                   @RequestParam(value = "page", required = false) Integer page,
                                                                   @RequestParam(value = "per_page", required = false) Integer perPage ) {
        Pageable pageable = getPageable(page, perPage);
        Page<AvailableCarParkDTO> availableCarParks = carParkAvailabilityService.getNearestAvailableCarParks(latitude, longitude, pageable);
        return availableCarParks.map(this::convertToCarParkSearchResponse);
    }

    private CarParkSearchResponse convertToCarParkSearchResponse(AvailableCarParkDTO dto) {
        return CarParkSearchResponse.builder()
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .totalLots(dto.getTotalLots())
                .lotsAvailable(dto.getLotsAvailable())
                .distance(dto.getDistance())
                .build();
    }

    private Pageable getPageable(Integer page, Integer perPage) {
        if (page == null) page = 0;
        if (perPage == null) perPage = 50;

        if (page < 0)
            throw new InvalidInputException("request.param.page.invalid", "Request parameter ''page'' should be greater than or equal to 0");

        if (perPage < 1)
            throw new InvalidInputException("request.param.perPage.invalid", "Request parameter ''per_page'' should be greater than or equal to 1");

        return PageRequest.of(page, perPage);
    }
}
