package com.example.demo.model.dto.request;


import com.example.demo.model.enums.CarType;
import com.example.demo.model.enums.Color;
import com.example.demo.model.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarInfoRequest {

    String brand;
    String model;
    Color color;
    Integer year;
    CarType carType;
    Long price;
    Boolean isNew;
}
