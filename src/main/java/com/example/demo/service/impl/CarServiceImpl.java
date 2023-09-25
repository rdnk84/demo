package com.example.demo.service.impl;

import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.repository.CarRepo;
import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.CarStatus;
import com.example.demo.service.CarService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final UserService userService;
    private final CarRepo carRepo;
    private final ObjectMapper mapper;

//    private List<CarInfoResponse> cars = new ArrayList<>();
//    private Long id = 0L;

    @Override
    public CarInfoResponse getCar(Long id) {

        Car car = carRepo.findById(id).orElse(new Car());

//        for(CarInfoResponse car : cars) {
//            if(id.equals(car.getId())){
//                return car;
//            }
//        }
        return mapper.convertValue(car, CarInfoResponse.class);
    }

    @Override
    public CarInfoResponse updateCar(Long id, CarInfoRequest request) {
        Car car = carRepo.findById(id).orElse(null);
        if(car == null){
            return null;
        }
car.setBrand(StringUtils.isBlank(request.getBrand()) ? car.getBrand() : request.getBrand());
        car.setModel(StringUtils.isBlank(request.getModel()) ? car.getModel() : request.getModel());
        car.setColor(request.getColor() == null ? car.getColor() : request.getColor());
        car.setYear(request.getYear() == null ? car.getYear() : request.getYear());
        car.setCarType(request.getCarType() == null ? car.getCarType() : request.getCarType());
        car.setPrice(request.getPrice() == null ? car.getPrice() : request.getPrice());
        car.setIsNew(request.getIsNew() == null ? car.getIsNew() : request.getIsNew());

        car.setStatus(CarStatus.UPDATED);
        car.setUpdatedAt(LocalDateTime.now());
        return null;
    }

    @Override
    public CarInfoResponse createCar(CarInfoRequest request) {
        Car car = mapper.convertValue(request, Car.class);
        car.setStatus(CarStatus.CREATED);
        car.setCreatedAt(LocalDateTime.now());
        Car save = carRepo.save(car);
        CarInfoResponse carCreated = mapper.convertValue(car, CarInfoResponse.class);
        return carCreated;
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepo.findById(id).orElse(null);
        if(car != null) {
            car.setUpdatedAt(LocalDateTime.now());
            car.setStatus(CarStatus.DELETED);
        }
    }

    @Override
    public List<CarInfoResponse> getAllCars() {

        return carRepo.findAll().stream()
                .filter(c -> c.getStatus() != CarStatus.DELETED)
                .map(c -> mapper.convertValue(c, CarInfoResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CarInfoResponse linkCarAndDriver(Long userId, Long carId) {
        CarInfoResponse car = getCar(carId);
        UserInfoResponse user = userService.getUser(userId);
        car.setUser(user);
        return car;
    }
}
