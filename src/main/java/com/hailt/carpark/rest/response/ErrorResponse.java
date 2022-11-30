package com.hailt.carpark.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Collections.singletonList;

@Data
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ErrorResponse {
    private List<ErrorMessage> errors;

    public ErrorResponse(String key, String message) {
        errors = singletonList(new ErrorMessage(key, message));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorMessage implements Serializable {
        private String key;
        private String message;
    }
}
