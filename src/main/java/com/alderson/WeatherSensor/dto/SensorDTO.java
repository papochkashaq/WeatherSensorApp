package com.alderson.WeatherSensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {

    @Size(min = 3, max = 30, message = "Name should be more than 2 and less than 30 symbols")
    @NotBlank(message = "Name should not be empty")
    private String name;
}
