package com.example.demo.service.impl;

import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    public List<UserInfoResponse> users;

    @Override
    public UserInfoResponse getUser(Long id) {
        return null;
    }

    @Override
    public UserInfoResponse updateUser(Long id, UserInfoRequest request) {
        return null;
    }

    @Override
    public UserInfoResponse createUser(UserInfoRequest request) {
        log.info("new User is created!");
        UserInfoResponse newUser = UserInfoResponse.builder()
                .id(100L)
                .age(request.getAge())
                .email(request.getEmail())
                .gender(request.getGender())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .password(request.getPassword())
                .build();
        users.add(newUser);
        return newUser;
    }

    @Override
    public UserInfoResponse deleteUser(Long id) {
        return null;
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return null;
    }
}
