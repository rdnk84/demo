package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.utils.PaginationUtil.getPageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final UserService userService;
    private final CarRepo carRepo;
    private final ObjectMapper mapper;

    @Value("${app.base-url}")
    private String baseUrl;

//    private List<CarInfoResponse> cars = new ArrayList<>();
//    private Long id = 0L;

//    @Override
//    public CarInfoResponse getCar(Long id) {

//        for(CarInfoResponse car : cars) {
//            if(id.equals(car.getId())){
//                return car;
//            }
//        }
//        return null;
//}

    @Override
    public CarInfoResponse getCar(Long id) {
        String errMsg = String.format("User with id %d not found", id);
        Car car = carRepo.findById(id).orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
        return mapper.convertValue(car, CarInfoResponse.class);
    }

    private Car getCarById(Long id) {
        String errMsg = String.format("User with id %d not found", id);
        Car car = carRepo.findById(id).orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
        return car;
    }

    @Override
    public CarInfoResponse updateCar(Long id, CarInfoRequest request) {
        String errMsg = String.format("User with id %d not found", id);
        Car car = carRepo.findById(id).orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
//        Car car = carRepo.findById(id).orElse(null);
//        if (car == null) {
//            return null;
//        }
        car.setBrand(StringUtils.isBlank(request.getBrand()) ? car.getBrand() : request.getBrand());
        car.setModel(StringUtils.isBlank(request.getModel()) ? car.getModel() : request.getModel());
        car.setColor(request.getColor() == null ? car.getColor() : request.getColor());
        car.setYear(request.getYear() == null ? car.getYear() : request.getYear());
        car.setCarType(request.getCarType() == null ? car.getCarType() : request.getCarType());
        car.setPrice(request.getPrice() == null ? car.getPrice() : request.getPrice());
        car.setIsNew(request.getIsNew() == null ? car.getIsNew() : request.getIsNew());

        car.setStatus(CarStatus.UPDATED);
        car.setUpdatedAt(LocalDateTime.now());
        Car save = carRepo.save(car);
        return mapper.convertValue(save, CarInfoResponse.class);
    }

    @Override
    public CarInfoResponse createCar(CarInfoRequest request) {
        carRepo.findByModelAndAndBrand(request.getModel(), request.getBrand())
                .ifPresent(u -> {
                    throw new CustomException("User with this email already exist", HttpStatus.BAD_REQUEST);
                });

        Car car = mapper.convertValue(request, Car.class);
        car.setStatus(CarStatus.CREATED);
        car.setCreatedAt(LocalDateTime.now());
        car = carRepo.save(car);
        CarInfoResponse carCreated = mapper.convertValue(car, CarInfoResponse.class);
        return carCreated;
    }

    @Override
    public void deleteCar(Long id) {
        String errMsg = String.format("User with id %d not found", id);
        Car car = carRepo.findById(id).orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));

        car.setUpdatedAt(LocalDateTime.now());
        car.setStatus(CarStatus.DELETED);
        carRepo.save(car);
    }

    @Override
    public List<CarInfoResponse> getAllCars() {

        return carRepo.findAll().stream()
                .filter(c -> c.getStatus() != CarStatus.DELETED)
                .map(c -> mapper.convertValue(c, CarInfoResponse.class))
                .collect(Collectors.toList());
    }

//    @Override
//    public CarInfoResponse linkCarAndDriver(Long userId, Long carId) {
//        CarInfoResponse car = getCar(carId);
//        UserInfoResponse user = userService.getUserDto(userId);
//        car.setUser(user);
//        return car;
//    }

    @Override
    public CarInfoResponse linkCarAndDriver(Long userId, Long carId) {
        Car car = getCarById(carId);
        if (car.getDriver() != null) {
            throw new CustomException("Driver already exists", HttpStatus.BAD_REQUEST);
        }
        User user = userService.getUser(userId);
        user.getCars().add(car);
        userService.updateCarList(user); //обновили в DB сведения о Юзере (ему добавилась новая машина)
        car.setDriver(user);// сущности Car приписали владельца
        carRepo.save(car);//обновили в DB сведения о данной машине (теперь у нее есть владелец)

        CarInfoResponse carInfoResponse = mapper.convertValue(car, CarInfoResponse.class);
        UserInfoResponse userInfoResponse = mapper.convertValue(user, UserInfoResponse.class);
        carInfoResponse.setUser(userInfoResponse); //обновили наш объект Car Dto добавив ему Юзера
        return carInfoResponse;
    }


//    @Override
//    public CarInfoResponse getCarByParameter(Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {
//        return null;
//    }

    public Page<CarInfoResponse> getCarsByParameter(Integer page, Integer perPage, String sort, Sort.Direction order, String brand, String model) {
//        PaginationUtil.getPageRequest(page, perPage, sort, order); //или можем просто заимпортировать метод статического класса
        Pageable pageRequest = getPageRequest(page, perPage, sort, order);
        Page<Car> carPageList;

        //тут- если фильтр передали и если нет
        if (StringUtils.isNotBlank(brand) && StringUtils.isNotBlank(model)) {
            carPageList = carRepo.findAllNotDeleted(pageRequest, brand, model);
        } else {
            if (StringUtils.isNotBlank(brand)) {
                carPageList = carRepo.findAllNotDeletedBrand(pageRequest, brand);
            } else if (StringUtils.isNotBlank(model)) {
                carPageList = carRepo.findAllNotDeletedModel(pageRequest, model);
            } else {
                carPageList = carRepo.findAllNotDeleted(pageRequest);
            }
        }
        //методом getContent() вытаскиваем весь контент в List
        List<CarInfoResponse> response = carPageList.getContent().stream()
                .map(c -> mapper.convertValue(c, CarInfoResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(response); //класс PageImpl реализует интерфейс Page
        //здесь пустой generic<> потому что мы выше уже указати конкретный тип <CarInfoResponse>
    }

    @Override
    public Page<CarInfoResponse> getAllCars(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = getPageRequest(page, perPage, sort, order);
        Page<Car> cars = carRepo.findAllNotDeleted(pageRequest);
        List<CarInfoResponse> response = cars.getContent().stream()
                .map(c -> mapper.convertValue(c, CarInfoResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(response);
    }


}
