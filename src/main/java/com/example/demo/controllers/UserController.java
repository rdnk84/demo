package com.example.demo.controllers;

import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserServiceImpl userService;

//    public UserController(UserServiceImpl userService) {
//        this.userService = userService;
//    }

    @PostMapping
    @Operation(summary = "Create user")
    public UserInfoResponse createUser(@RequestBody UserInfoRequest request) {
        return userService.createUser(request);
    }

    @Operation(summary = "Get all users with filter parameter")
    @GetMapping("/all")
    public Page<UserInfoResponse> allUsers(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer perPage,
                                           @RequestParam(defaultValue = "email") String sort,
                                           @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                           @RequestParam(required = false) String filter) {

        return userService.getAllUsers(page, perPage, sort, order, filter);
    }

    @Operation(summary = "Get all users with several filter parameters or without")
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

    @Operation(summary = "Get users with first name")
    @GetMapping("/name")
    public List<UserInfoResponse> usersByFirstName(@RequestParam(required = false) String firstName) {
        return userService.usersByFirstName(firstName);
    }

    @Operation(summary = "Get User by id")
    @GetMapping("/{id}")
    public UserInfoResponse getUser(@RequestHeader("api-key") String apiKey, @PathVariable Long id) {
        return userService.getUserDto(apiKey, id);
    }

    @Operation(summary = "Change user parameters")
    @PutMapping("/{id}")
    public UserInfoResponse changeUser(@PathVariable Long id, @RequestBody UserInfoRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(summary = "Change 'user status' as 'deleted'")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Get all cars by User")
    @GetMapping("/carsByUser/{userId}")
    public List<CarInfoResponse> carsByUser(@PathVariable Long userId) {
        return userService.getCarsByUser(userId);
    }
}
