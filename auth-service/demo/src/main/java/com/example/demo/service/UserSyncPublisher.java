package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dto.AppUserSyncDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserSyncPublisher {
    private final RabbitTemplate rabbitTemplate;
    public UserSyncPublisher(RabbitTemplate rabbitTemplate) { this.rabbitTemplate = rabbitTemplate; }

    public void sendUserCreated(UUID userId, String username, String email, String role, String password) {
        AppUserSyncDTO dto = AppUserSyncDTO.createUser(username, email, role, password);
        dto.setUserId(userId);
        rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHANGE, "auth.user.create", dto);
    }
}