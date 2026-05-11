package com.alderson.WeatherSensor.services;

import com.alderson.WeatherSensor.dto.MeasurementDTO;
import com.alderson.WeatherSensor.models.Measurement;
import com.alderson.WeatherSensor.models.Sensor;
import com.alderson.WeatherSensor.repositories.MeasurementRepository;
import com.alderson.WeatherSensor.utils.SensorNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasurementService {

    private final MeasurementRepository repository;
    private final SensorService sensorService;
    private final ModelMapper mapper;

    @Autowired
    public MeasurementService(MeasurementRepository repository, SensorService sensorService, ModelMapper mapper) {
        this.repository = repository;
        this.sensorService = sensorService;
        this.mapper = mapper;
    }

    public List<MeasurementDTO> findAll() {
        return repository.findAll()
                .stream().map(m -> convertToMeasurementDTO(m))
                .collect(Collectors.toList());
    }

    public List<MeasurementDTO> findByName(String sensorName) {
        Sensor sensor = sensorService.findByName(sensorName).orElseThrow(() -> new SensorNotFoundException("Sensor with this name not found"));
        return repository.findBySensor(sensor)
                .stream()
                .map(m -> convertToMeasurementDTO(m))
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(MeasurementDTO measurementDTO) {
        Sensor sensor = sensorService.findByName(measurementDTO.getSensor().getName()).orElseThrow(() -> new SensorNotFoundException("Sensor with this name not found"));
        Measurement measurement = convertToMeasurement(measurementDTO);
        measurement.setSensor(sensor);
        measurement.setTime(LocalDateTime.now());
        repository.save(measurement);
    }

    public long rainyDaysCount() {
        return repository.findAll()
                .stream()
                .filter(measurement -> measurement.isRaining() == true)
                .count();
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        return mapper.map(measurementDTO, Measurement.class);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return mapper.map(measurement, MeasurementDTO.class);
    }

}
