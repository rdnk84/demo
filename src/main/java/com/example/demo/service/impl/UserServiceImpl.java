package com.example.demo.service.impl;

import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepo;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.UserStatus;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ObjectMapper mapper;

    @Override
    public UserInfoResponse getUser(Long id) {
        User user = userRepo.findById(id).orElse(new User());
//        for(UserInfoResponse user : users) {
//            if(id.equals(user.getId())) {
//                return user;
//            }
//        }
        return mapper.convertValue(user, UserInfoResponse.class);
    }

    @Override
    public UserInfoResponse updateUser(Long id, UserInfoRequest request) {
        User user = userRepo.findById(id).orElse(null);
        if(user == null) {
            return null;
        }
        user.setEmail(StringUtils.isBlank(request.getEmail()) ? user.getEmail() : request.getEmail());
        user.setGender(request.getGender() == null ? user.getGender() : request.getGender());
        user.setAge(user.getAge() == null ? user.getAge() : request.getAge());
        user.setFirstName(StringUtils.isBlank(request.getFirstName()) ? user.getFirstName() : request.getFirstName());
        user.setLastName(StringUtils.isBlank(request.getLastName()) ? user.getLastName() : request.getLastName());
        user.setMiddleName(StringUtils.isBlank(request.getMiddleName()) ? user.getMiddleName() : request.getMiddleName());
        user.setPassword(StringUtils.isBlank(request.getPassword()) ? user.getPassword() : request.getPassword());

        user.setStatus(UserStatus.UPDATED);
        user.setUpdatedAt(LocalDateTime.now());

        User save = userRepo.save(user);
        return mapper.convertValue(save, UserInfoResponse.class);
    }

    @Override
    public UserInfoResponse createUser(UserInfoRequest request) {
        User user = mapper.convertValue(request, User.class);
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.CREATED);

        User save = userRepo.save(user);
        UserInfoResponse createdUser = mapper.convertValue(save, UserInfoResponse.class);
        return createdUser;
    }


    //создаем Юзера без базы данных и сохраняем в список пока тут, в классе
    //    private List<UserInfoResponse> users = new ArrayList<>();
//    private Long id = 0L;
//    public UserInfoResponse createUser(UserInfoRequest request) {
//        log.info("new User is created!");
//        UserInfoResponse newUser = UserInfoResponse.builder()
//                .id(++id)
//                .age(request.getAge())
//                .email(request.getEmail())
//                .gender(request.getGender())
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .middleName(request.getMiddleName())
//                .password(request.getPassword())
//                .build();
//        users.add(newUser);
//        return newUser;
//    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if(user != null) {
            user.setStatus(UserStatus.DELETED);
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
        }
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .map(u -> mapper.convertValue(u, UserInfoResponse.class))
                .collect(Collectors.toList());
    }
}
