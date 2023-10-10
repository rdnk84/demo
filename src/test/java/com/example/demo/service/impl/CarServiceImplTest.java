package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.CarRepo;
import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.enums.CarStatus;
import com.example.demo.model.enums.CarType;
import com.example.demo.model.enums.Color;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceImplTest {
    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepo carRepo;

    @Mock
    private UserService userService;
    @Spy
    private ObjectMapper objectMapper;


    @Test
    public void linkCarAndDriver() {

        User user = new User();
        user.setId(1L);
        when(userService.getUser(1L)).thenReturn(user);

        Car car = new Car();
        car.setId(1L);
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));

        Car car1 = new Car();
        car1.setId(3L);
        car1.setBrand("B1");

        List<Car> cars = new ArrayList<>();
        cars.add(car1);
        cars.add(car);
        user.setCars(cars);

        //добавили юзеру машины
//        user.setCars(Collections.singletonList(car)); //добавили лист из одного элемента
        Integer carsBefore = user.getCars().size();
        when(userService.updateCarList(user)).thenReturn(user);

        Integer carsAfter = user.getCars().size();

        car.setDriver(user);
        when(carRepo.save(any(Car.class))).thenReturn(car);

        CarInfoResponse response = carService.linkCarAndDriver(1L, 1L);
        assertEquals(response.getUser().getId(), car.getDriver().getId());
//        assertEquals(carsAfter, 1);
    }

    @Test(expected = CustomException.class)
    public void driver_exists() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser(1L)).thenReturn(user);

        Car car = new Car();
        car.setId(1L);
        car.setDriver(user);
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));

        User user1 = new User();
        user1.setId(2L);
        when(userService.getUser(2L)).thenReturn(user1);
        car.setDriver(user1);
        when(carRepo.save(any(Car.class))).thenThrow(CustomException.class);
        carService.linkCarAndDriver(2L, 1L);
    }

    @Test
    public void getCar() {
        Long id = 1L;
        Car car = new Car();
        car.setId(1l);
        when(carRepo.findById(id)).thenReturn(Optional.of(car));
        carService.getCar(id);
    }

    @Test(expected = CustomException.class)
    public void getCar_badId() {
        Car car = new Car();
        car.setId(1l);
        when(carRepo.save(car)).thenReturn(car);
        Long id = 2L;
        when(carRepo.findById(id)).thenThrow(CustomException.class);
        carService.getCar(id);
    }

    @Test
    public void updateCar() {
        CarInfoRequest request = new CarInfoRequest();
        request.setBrand("CarBrand");

        Car car = new Car();
        car.setBrand("Mercedes");
        car.setCarType(CarType.SEDAN);
        car.setPrice(100L);
        car.setModel("AA-1");
        car.setColor(Color.GREEN);
        car.setYear(2000);
        car.setIsNew(true);
        car.setId(1L);
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(carRepo.save(any(Car.class))).thenReturn(car);
        CarInfoResponse response = carService.updateCar(1L, request);
        assertEquals(request.getBrand(), response.getBrand());
        assertEquals(car.getModel(), response.getModel());
    }

    @Test
    public void createCar() {
        CarInfoRequest request = new CarInfoRequest();
        request.setBrand("newBrand");
        request.setCarType(CarType.CONVERTABLE);
        request.setPrice(100L);
        request.setModel("AA-11");
        request.setYear(2000);
        request.setColor(Color.BLUE);
        request.setIsNew(true);

        Car car = new Car();
        car.setId(1L);

        when(carRepo.save(any(Car.class))).thenReturn(car);

        CarInfoResponse response = carService.createCar(request);

        assertEquals(response.getId(), car.getId());
    }

    @Test
    public void deleteCar() {
        Car car = new Car();
        car.setId(1L);
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        carService.deleteCar(1L);
        verify(carRepo, times(1)).save(any(Car.class));
        assertEquals(CarStatus.DELETED, car.getStatus());
    }

    @Test
    public void getAllCars() {

    }


    @Test
    public void getCarsByParameter() {
    }

    @Test
    public void testGetAllCars() {
    }
}