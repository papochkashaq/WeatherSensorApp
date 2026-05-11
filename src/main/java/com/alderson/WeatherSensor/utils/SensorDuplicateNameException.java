package com.alderson.WeatherSensor.utils;

public class SensorDuplicateNameException extends RuntimeException {
    public SensorDuplicateNameException(String message) {
        super(message);
    }
}
