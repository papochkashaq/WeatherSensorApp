package com.alderson.WeatherSensor.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "measurement")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "value")
    private double value;

    @Column(name = "raining")
    private boolean raining;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(name = "time")
    LocalDateTime time;
}
