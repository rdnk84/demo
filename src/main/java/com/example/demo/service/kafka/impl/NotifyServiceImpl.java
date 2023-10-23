package com.example.demo.service.kafka.impl;

import com.example.demo.service.kafka.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    //здесь у нас настройки отправки сообщений
    private final KafkaTemplate<String, String> template;

    @Value("${kafka.topics.notification}")
    private String notificationTopic;
    @Override
    public void sendNotification(String msg) {

        try {
            template.send(notificationTopic, String.valueOf(UUID.randomUUID()), msg)
                    .addCallback(result -> {
                        if(result != null) {
                            log.info("notification sent successfully");
                        }
                    }, ex -> log.error("notification error"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
