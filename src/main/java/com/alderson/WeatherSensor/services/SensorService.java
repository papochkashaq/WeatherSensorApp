package com.alderson.WeatherSensor.services;

import com.alderson.WeatherSensor.dto.SensorDTO;
import com.alderson.WeatherSensor.models.Sensor;
import com.alderson.WeatherSensor.repositories.SensorRepository;
import com.alderson.WeatherSensor.utils.SensorDuplicateNameException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SensorService {

    private SensorRepository repository;
    private ModelMapper mapper;

    @Autowired
    public SensorService(SensorRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public void save(SensorDTO sensorDTO) {
        if (findByName(sensorDTO.getName()).isPresent()) {
            throw new SensorDuplicateNameException("Sensor with this name already exists");
        }
        repository.save(convertToSensor(sensorDTO));
    }

    public Optional<Sensor> findByName(String name) {
        return repository.findByName(name);
    }

    public Sensor convertToSensor(SensorDTO sensorDTO) {
        return mapper.map(sensorDTO, Sensor.class);
    }

    public SensorDTO convertToSensorDTO(Sensor sensor) {
        return mapper.map(sensor, SensorDTO.class);
    }
}
