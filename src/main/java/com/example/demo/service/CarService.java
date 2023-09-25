package com.example.demo.service;

import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;

import java.util.List;

public interface CarService {

    CarInfoResponse getCar(Long id);

    CarInfoResponse updateCar(Long id, CarInfoRequest request);

    CarInfoResponse createCar(CarInfoRequest request);

    void deleteCar(Long id);

    List<CarInfoResponse> getAllCars();

    CarInfoResponse linkCarAndDriver(Long userId, Long carId);
}
