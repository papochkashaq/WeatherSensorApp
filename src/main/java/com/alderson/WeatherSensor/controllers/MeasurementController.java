package com.alderson.WeatherSensor.controllers;

import com.alderson.WeatherSensor.dto.MeasurementDTO;
import com.alderson.WeatherSensor.services.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/measurements")
public class MeasurementController {

    private MeasurementService service;

    @Autowired
    public MeasurementController(MeasurementService service) {
        this.service = service;
    }

    @GetMapping
    public List<MeasurementDTO> findByName(@RequestParam(value = "sensorName", required = false) String sensorName) {
        if (sensorName == null) {
            return service.findAll();
        }
        return service.findByName(sensorName);
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addMeasurements(@Valid @RequestBody MeasurementDTO measurementDTO) {
        service.save(measurementDTO);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping("/rainyDaysCount")
    public long rainyDaysCount() {
        return service.rainyDaysCount();
    }

}
