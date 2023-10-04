package com.example.demo.service;

import com.example.demo.model.db.entity.User;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {

    UserInfoResponse getUserDto(String apiKey, Long id);

    User getUser(Long id);

    UserInfoResponse updateUser(Long id, UserInfoRequest request);
    UserInfoResponse createUser(UserInfoRequest request);
    void deleteUser(Long id);

    Page<UserInfoResponse> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order, String filter);

    User updateCarList(User user);

    List<CarInfoResponse> getCarsByUser(Long id);

    Page<UserInfoResponse> usersByQuery(Integer page, Integer perPage, String sort, Sort.Direction order,String firstName, String lastName, String email);

    public List<UserInfoResponse> usersByFirstName(String name);

    void invalidateSessions();

    void sendMsg();
}
