package com.alderson.WeatherSensor.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SensorErrorResponse {

    private String message;
    private LocalDateTime time;
}
