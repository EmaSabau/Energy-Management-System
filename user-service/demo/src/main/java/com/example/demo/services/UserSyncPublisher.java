package com.example.demo.services;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dtos.AppUserSyncDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserSyncPublisher {
    private final RabbitTemplate rabbitTemplate;

    public UserSyncPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserCreated(UUID userId, String username, String email, String role, String password) {
        AppUserSyncDTO dto = AppUserSyncDTO.createUser(username, email, role, password);
        dto.setUserId(userId);
        rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHANGE, "user.user.create", dto);
    }

    public void sendUserUpdated(UUID userId, String oldUsername, String newUsername, String email, String role) {
        AppUserSyncDTO dto = AppUserSyncDTO.updateUser(userId, oldUsername, newUsername, email, role);
        rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHANGE, "user.user.update", dto);
    }

    public void sendUserDeleted(UUID userId, String username) {
        AppUserSyncDTO dto = AppUserSyncDTO.deleteUser(userId, username);
        rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHANGE, "user.user.delete", dto);
    }
}