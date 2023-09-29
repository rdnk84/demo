package com.example.demo.controllers;

import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserServiceImpl userService;

//    public UserController(UserServiceImpl userService) {
//        this.userService = userService;
//    }

    @PostMapping
    public UserInfoResponse createUser(@RequestBody UserInfoRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/all")
    public Page<UserInfoResponse> allUsers(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer perPage,
                                           @RequestParam(defaultValue = "email") String sort,
                                           @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                           @RequestParam(required = false) String filter) {

        return userService.getAllUsers(page, perPage, sort, order, filter);
    }

    @GetMapping("/bySearch")
    public Page<UserInfoResponse> usersByQuery(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer perPage,
                                               @RequestParam(defaultValue = "email") String sort,
                                               @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                               @RequestParam(required = false) String firstName,
                                               @RequestParam(required = false) String lastName,
                                               @RequestParam(required = false) String email) {
        return userService.usersByQuery(page, perPage, sort, order, firstName, lastName, email);
    }

    @GetMapping("/name")
    public List<UserInfoResponse> usersByFirstName(@RequestParam(required = false) String firstName) {
        return userService.usersByFirstName(firstName);
    }

    @GetMapping("/{id}")
    public UserInfoResponse getUser(@PathVariable Long id) {

        return userService.getUserDto(id);
    }

    @PutMapping("/{id}")
    public UserInfoResponse changeUser(@PathVariable Long id, @RequestBody UserInfoRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/carsByUser/{userId}")
    public List<CarInfoResponse> carsByUser(@PathVariable Long userId) {
        return userService.getCarsByUser(userId);
    }
}
