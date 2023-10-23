package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;

import com.example.demo.model.auth.userDetailsImpl;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepo;
import com.example.demo.model.db.repository.UserSearchDao;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.UserStatus;
import com.example.demo.service.UserService;
import com.example.demo.service.kafka.NotifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.config.secret.Constants.validateKey;
import static com.example.demo.utils.PaginationUtil.getPageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final ObjectMapper mapper;
    private final UserSearchDao userSearchDao;
    private final NotifyService notifyService;

    @Value("${app.base-url}")
    private String baseUrl;
//    @Override
//    public UserInfoResponse getUser(Long id) {

//        for(UserInfoResponse user : users) {
//            if(id.equals(user.getId())) {
//                return user;
//            }
//        }
//        return null;
//    }

//    @Override
//    public UserInfoResponse getUser(Long id) {
//        User user = userRepo.findById(id).orElse(new User());
//        return mapper.convertValue(user, UserInfoResponse.class);
//    }

    @Override
    public UserInfoResponse getUserDto(String apiKey, Long id) {
        validateKey(apiKey);
        String errMsg = String.format("User with id %d not found", id);
        userRepo.findById(id)
                .orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
        User user = userRepo.findById(id).orElse(new User());
        return mapper.convertValue(user, UserInfoResponse.class);
    }

    @Override
    public User getUser(Long id) {
        String errMsg = String.format("User with id %d not found", id);
        return userRepo.findById(id)
                .orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
    }

    @Override
    public UserInfoResponse updateUser(Long id, UserInfoRequest request) {

        User user = getUser(id);

        String email = request.getEmail();
        if(StringUtils.isBlank(email)) {
            email = user.getEmail();
        } else if(!EmailValidator.getInstance().isValid(email)) {
            throw new CustomException("invalid email", HttpStatus.BAD_REQUEST);
        } else {
            email = request.getEmail();
        }
        user.setEmail(email);
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

        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            throw new CustomException("invalid email", HttpStatus.BAD_REQUEST);
        }
        String email = request.getEmail().trim();
        userRepo.findByEmail(email)
                .ifPresent(u -> {
                    throw new CustomException("User with this email already exist", HttpStatus.BAD_REQUEST);
                });
        if (StringUtils.isBlank(request.getLastName()) || StringUtils.isBlank(request.getFirstName()) || StringUtils.isBlank(request.getPassword())) {
            throw new CustomException("Some of highlighted fields are blank", HttpStatus.BAD_REQUEST);
        }
        User user = mapper.convertValue(request, User.class);
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.CREATED);

        User save = userRepo.save(user);
        UserInfoResponse createdUser = mapper.convertValue(save, UserInfoResponse.class);
        notifyService.sendNotification(String.format("User %s is added", user.getFirstName()));
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
        String errMsg = String.format("User with id %d not found", id);

        User user = userRepo.findById(id)
                .orElseThrow(() -> new CustomException(errMsg, HttpStatus.NOT_FOUND));
        user.setStatus(UserStatus.DELETED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public Page<UserInfoResponse> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {

        Pageable pageRequest = getPageRequest(page, perPage, sort, order);
        List<User> pageList;
        if (StringUtils.isBlank(filter)) {
            pageList = userRepo.findAllNotDeleted(pageRequest);
        } else {
            pageList = userRepo.findAllNotDeleted(pageRequest, filter);
        }
        List<UserInfoResponse> response = pageList.stream()
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .map(u -> mapper.convertValue(u, UserInfoResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(response);
    }


    @Override
    public User updateCarList(User user) {
        return userRepo.save(user);
    }

    @Override
    public List<CarInfoResponse> getCarsByUser(Long id) {
        User user = getUser(id);
        return user.getCars().stream()
                .map(c -> mapper.convertValue(c, CarInfoResponse.class))
                .collect(Collectors.toList());
    }
//мой кастомный метода с использованием Criteria API
    @Override
    public Page<UserInfoResponse> usersByQuery(Integer page, Integer perPage, String sort, Sort.Direction order, String firstName, String lastName, String email) {

        List<UserInfoResponse> users;
        Pageable pageRequest = getPageRequest(page, perPage, sort, order);

        if (firstName == null && lastName == null && email == null) {
            users = userRepo.findAllNotDeleted(pageRequest).stream()
                    .map(u -> mapper.convertValue(u, UserInfoResponse.class))
                    .collect(Collectors.toList());
            return new PageImpl<>(users);
        } else {
            users = userSearchDao.usersByCriteria(pageRequest, firstName, lastName, email).stream()
                    .map(u -> mapper.convertValue(u, UserInfoResponse.class))
                    .collect(Collectors.toList());
        }
        return new PageImpl<>(users);
    }

    @Override
    public List<UserInfoResponse> usersByFirstName(String name) {
        List<UserInfoResponse> users;
        users = userRepo.findByFirstName(name).stream()
                .map(u -> mapper.convertValue(u, UserInfoResponse.class))
                .collect(Collectors.toList());
        return users;
    }


    //в классе JobService прописано расписания вызова этих методов
    @Override
    public void invalidateSessions() {

    }

    @Override
    public void sendMsg() {

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow();
        return new userDetailsImpl(user);
    }


}
