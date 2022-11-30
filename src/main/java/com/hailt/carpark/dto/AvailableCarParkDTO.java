package com.hailt.carpark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableCarParkDTO {
    private String address;
    private String coordinateAsText;
    private Integer totalLots;
    private Integer lotsAvailable;
    private Double distance;

    public Double getLatitude() {
        String latLongAsText = getLatLongAsText();
        return Double.parseDouble(latLongAsText.split(" ")[1]);
    }

    public Double getLongitude() {
        String latLongAsText = getLatLongAsText();
        return Double.parseDouble(latLongAsText.split(" ")[0]);
    }

    private String getLatLongAsText() {
        return coordinateAsText.substring(coordinateAsText.lastIndexOf("(") + 1, coordinateAsText.lastIndexOf(")"));
    }
}
