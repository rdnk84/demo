package com.example.demo.controllers;

import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.CarServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cars")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarServiceImpl carService;
    private final UserService userService;

    @Operation(summary = "Create a car")
    @PostMapping
    public CarInfoResponse createCar(@RequestBody CarInfoRequest request) {
        return carService.createCar(request);
    }

//    @GetMapping("/all")
//    public List<CarInfoResponse> allCars() {
//        return carService.getAllCars();
//    }

    @Operation(summary = "Get cars by brand and model")
    @GetMapping("/all")
    public Page<CarInfoResponse> carsByParameter(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer perPage,
                                         @RequestParam(defaultValue = "price") String sort,
                                         @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                         @RequestParam(required = false) String brand,
                                         @RequestParam(required = false) String model) {

        return carService.getCarsByParameter(page, perPage, sort, order, brand, model);
    }

    @Operation(summary = "Get all cars")
    public Page<CarInfoResponse> getAll(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer perPage,
                                        @RequestParam(defaultValue = "price") String sort,
                                        @RequestParam(defaultValue = "ASC") Sort.Direction order){
        return carService.getAllCars(page, perPage, sort, order);

    }

    @Operation(summary = "Get all cars by id")
    @GetMapping("/{id}")
    public CarInfoResponse getCar(@PathVariable Long id) {
        return carService.getCar(id);
    }

    @Operation(summary = "Change any of car parameters")
    @PutMapping("/{id}")
    public CarInfoResponse changeCar(@PathVariable Long id, @RequestBody CarInfoRequest request) {
        return carService.updateCar(id, request);
    }

    @Operation(summary = "Change 'car status' as 'deleted'")
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }

    @Operation(summary = "Define a driver to a car")
    @PostMapping("/linkCarAndDriver/{userId}/{carId}")
    public CarInfoResponse carAndDriver(@PathVariable Long userId, @PathVariable Long carId) {
        return carService.linkCarAndDriver(userId, carId);
    }


}
