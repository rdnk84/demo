package com.example.demo.model.db.entity;

import com.example.demo.model.enums.CarStatus;
import com.example.demo.model.enums.CarType;
import com.example.demo.model.enums.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String brand;
    String model;

    @Enumerated(EnumType.STRING)
    Color color;
    Integer year;

    @Enumerated(EnumType.STRING)
    CarType carType;

    Long price;

    @Column(name = "is_new")
    Boolean isNew;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    CarStatus status;

    @ManyToOne
    @JsonBackReference(value = "driver_cars")
    User driver;
}
