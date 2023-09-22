package com.example.demo.service;

import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;

import java.util.List;

public interface UserService {

    UserInfoResponse getUser(Long id);
    UserInfoResponse updateUser(Long id, UserInfoRequest request);
    UserInfoResponse createUser(UserInfoRequest request);
    UserInfoResponse deleteUser(Long id);

    List<UserInfoResponse> getAllUsers();
}
