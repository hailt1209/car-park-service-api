package com.hailt.carpark.exception;

import lombok.Getter;

import static java.text.MessageFormat.format;

@Getter
public class CarParkApiException extends RuntimeException {
    private final String key;
    private final String message;

    public CarParkApiException(String key, String message, Object... msgVars) {
        this.key = key;
        this.message = format(message, msgVars);
    }
}
