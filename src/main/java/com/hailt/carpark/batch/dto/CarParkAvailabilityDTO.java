package com.hailt.carpark.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarParkAvailabilityDTO {
    private String carParkNo;
    private long totalLots;
    private long lotsAvailable;
}
