package com.alderson.WeatherSensor.repositories;

import com.alderson.WeatherSensor.models.Measurement;
import com.alderson.WeatherSensor.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findAll();

    List<Measurement> findBySensor(Sensor sensor);
}
