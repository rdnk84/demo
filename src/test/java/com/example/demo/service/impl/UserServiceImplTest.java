package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepo;
import com.example.demo.model.db.repository.UserSearchDao;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

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

    }

    @Test
    public void updateUser() {
    }

    @Test
    public void updateUser_byEmail() {
        UserInfoRequest request = new UserInfoRequest();
        Long userId = 1L;
        request.setEmail("test2@test.com");

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1l)).thenReturn(Optional.of(user));
        UserInfoResponse response = userService.updateUser(userId, request);
        assertEquals(Optional.of(response.getId()), Optional.of(user.getId()));
    }

    @Test
    public void createUser() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@test.com");


        User user = new User();
        user.setId(1L);

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
        userService.deleteUser(1L);
        verify(userRepo, times(1)).save(any(User.class));
        assertEquals(UserStatus.DELETED, user.getStatus());

    }

    @Test
    public void getAllUsers() {

    }

    @Test
    public void updateCarList() {
    }

    @Test
    public void getCarsByUser() {
    }

    @Test
    public void usersByQuery() {
    }

    @Test
    public void usersByFirstName() {
        String firstName = "firstName";

    }

    @Test
    public void invalidateSessions() {
    }

    @Test
    public void sendMsg() {
    }
}