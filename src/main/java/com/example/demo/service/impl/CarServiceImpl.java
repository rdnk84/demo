package com.example.demo.service.impl;

import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.service.CarService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final UserService userService;

    private List<CarInfoResponse> cars;

    @Override
    public CarInfoResponse getCar(Long id) {
        return null;
    }

    @Override
    public CarInfoResponse updateCar(Long id, CarInfoRequest request) {
        return null;
    }

    @Override
    public CarInfoResponse createCar(CarInfoRequest request) {

        CarInfoResponse newCar = CarInfoResponse.builder()
                .id(101L)
                .brand(request.getBrand())
                .carType(request.getCarType())
                .color(request.getColor())
                .model(request.getModel())
                .year(request.getYear())
                .isNew(request.getIsNew())
                .build();
        cars.add(newCar);
        return newCar;
    }

    @Override
    public CarInfoResponse deleteCar(Long id) {
        return null;
    }

    @Override
    public List<CarInfoResponse> getAllCars() {
        return null;
    }

    @Override
    public CarInfoResponse linkCarAndDriver(Long userId, Long carId) {
        CarInfoResponse car = getCar(carId);
        UserInfoResponse user = userService.getUser(userId);
        car.setUser(user);
        return car;
    }
}
