package com.alderson.WeatherSensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementDTO {

    @NotNull(message = "Value 'value' should not be empty")
    @Min(value = -100, message = "Value should be more than -100")
    @Max(value = 100, message = "Value should be less than 100")
    private Double value;

    @NotNull(message = "Value 'raining' should not be empty")
    private Boolean raining;

    @NotNull(message = "Value 'sensor' should not be empty")
    @Valid
    private SensorDTO sensor;

}
