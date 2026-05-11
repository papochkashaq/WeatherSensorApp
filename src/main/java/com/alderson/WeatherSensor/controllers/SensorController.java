package com.alderson.WeatherSensor.controllers;

import com.alderson.WeatherSensor.dto.SensorDTO;
import com.alderson.WeatherSensor.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    private SensorService service;

    @Autowired
    public SensorController(SensorService service) {
        this.service = service;
    }

    @PostMapping("/registration")
    public ResponseEntity<SensorDTO> registration(@Valid @RequestBody SensorDTO sensorDTO) {
        service.save(sensorDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
