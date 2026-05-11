package com.alderson.WeatherSensor.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sensor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "sensor")
    private List<Measurement> measurements;

}
