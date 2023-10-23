package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepo;
import com.example.demo.model.db.repository.UserSearchDao;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.Gender;
import com.example.demo.model.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserSearchDao userSearchDao;

    @Spy
    private ObjectMapper mapper;


    @Test(expected = CustomException.class)
    public void getUserDto_badApiKey() {
        Long userId = 1L;
        String apiKey = "kjfje";
        userService.getUserDto(apiKey, userId);
    }

    @Test(expected = CustomException.class)
    public void getUserDto_badId() {
        Long userId = 10L;
        String apiKey = "kjfje";
        when(userRepo.findById(any())).thenThrow(CustomException.class);

        userService.getUserDto(apiKey, userId);
    }

    @Test
    public void getUser() {

        Long id = 1L;
        User user = new User();
        user.setId(1L);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        userService.getUser(id);
        assertEquals(id, user.getId());
    }

    @Test
    public void updateUser() {

        UserInfoRequest request = new UserInfoRequest();
        request.setAge(88);

        User user = new User();
        user.setId(1L);
        user.setAge(35);
        user.setGender(Gender.FEMALE);
        user.setFirstName("Sidra");
        user.setLastName("Sidorova");
        user.setMiddleName("kdkdkd");
        user.setEmail("ss@gmail.com");
        user.setPassword("1234");
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        when(userRepo.save(any(User.class))).thenReturn(user);

        UserInfoResponse response = userService.updateUser(user.getId(), request);

        assertEquals(request.getAge(), response.getAge());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    public void updateUser_byEmail() {
        UserInfoRequest request = new UserInfoRequest();
        Long userId = 1L;
        request.setEmail("test2@test.com");

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1l)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        UserInfoResponse response = userService.updateUser(userId, request);
        assertEquals(request.getEmail(), response.getEmail());
    }

    @Test
    public void createUser() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@test.com");
        request.setPassword("jjfjf");
        request.setFirstName("ddd");
        request.setLastName("aaa");

        User user = new User();
        user.setId(1L);
        //в каждом таком выражении(специальное для этих тестов) - это мы обращаемся к "Мок" серверу, к нашей "заглушке"
        when(userRepo.save(any(User.class))).thenReturn(user);

        UserInfoResponse response = userService.createUser(request);
        assertEquals(Optional.of(response.getId()), Optional.of(user.getId()));
    }

    @Test(expected = CustomException.class)
    public void createUser_badEmail() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@-test.com");
        userService.createUser(request);
    }

    @Test(expected = CustomException.class)
    public void createUser_userExists() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@test.com");

        User user = new User();
        user.setId(1L);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        userService.createUser(request);

    }

    @Test(expected = CustomException.class)
    public void createUser_emptyFields() {
        UserInfoRequest request = new UserInfoRequest();
        userService.createUser(request);
    }

    @Test(expected = CustomException.class)
    public void deleteUser_badId() {
        Long userId = 10L;

        when(userRepo.findById(any())).thenThrow(CustomException.class);
        userService.deleteUser(userId);
    }

    @Test
    public void deleteUser() {
        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        //здесь мы прогоняем нашего юзера через этот метод в сервисе,где он сохраняется с новым статусом
        userService.deleteUser(1L);

        //здесь мы проверяем - была и ходка в репозиторий и сколько раз и тоже сохраняем нашего юзера
        //"times" показывает сколько раз был вызван "save"
        //а дальше .save - мы как бы вызываем userRepo.save и сохраняем наш объект
        verify(userRepo, times(1)).save(any(User.class));
        assertEquals(UserStatus.DELETED, user.getStatus());

    }

    @Test
    public void getAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        List<User> users = (List.of(user));
        when(userRepo.findAllNotDeleted(any(Pageable.class), anyString())).thenReturn(users);

        Page<UserInfoResponse> result = userService.getAllUsers(1, 2, "email", Sort.Direction.DESC, "someFilter");
        assertEquals(result.getNumberOfElements(), 1);
    }


    @Test
    public void updateCarList() {
        User user = new User();
        user.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        Car car = new Car();
//        List<Car> cars = new ArrayList<>();
//        cars.add(car);
        List<Car> cars = (List.of(car));
        user.setCars(cars);
        userService.updateCarList(user);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    public void getCarsByUser() {

    }

    @Test
    public void usersByQuery() {

    }

    @Test
    public void usersByFirstName() {
        String firstName = "Ivan";
        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");

        List<User> users = (List.of(user));
        when(userRepo.findByFirstName(anyString())).thenReturn(users);
       List<UserInfoResponse> result = userService.usersByFirstName(firstName);
        assertEquals(result.get(0).getFirstName(), firstName);
    }

    @Test
    public void invalidateSessions() {
    }

    @Test
    public void sendMsg() {
    }
}