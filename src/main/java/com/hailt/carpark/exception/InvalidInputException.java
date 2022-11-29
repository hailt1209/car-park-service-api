package com.hailt.carpark.exception;

public class InvalidInputException extends CarParkApiException {
    public InvalidInputException(String key, String message, Object... msgVars) {
        super(key, message, msgVars);
    }
}
