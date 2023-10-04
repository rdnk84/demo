package com.example.demo.jobs;

import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final UserService userService;

    @Scheduled(cron = "0 0 3 ? * 2/2") //метод(который прерывает сессии у нас) отрабатывает 2 через два (дня) в три ночи
    public void invalidateSessions() {
        userService.invalidateSessions();
    }

    //или еще можно так
    @Scheduled(fixedDelay = 3000) //метод отрабатывает каждые три секунды
    public void sendMsg() {
        userService.sendMsg();
    }
}
