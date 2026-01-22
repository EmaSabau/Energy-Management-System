package com.example.demo.listener;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dto.AppUserSyncDTO;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserSyncListener {

    private final UserRepository userRepository;

    public UserSyncListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitConfig.MONITORING_USER_SYNC_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleUserSync(AppUserSyncDTO dto) {
        System.out.println(" Received user sync in Monitoring: " + dto.getUsername());
        try {
            switch (dto.getAction()) {
                case CREATE:
                case UPDATE:
                    User user = new User(dto.getUserId().toString(), dto.getUsername());
                    userRepository.save(user);
                    System.out.println(" User saved in local Monitoring DB: " + dto.getUsername());
                    break;
                case DELETE:
                    userRepository.deleteById(dto.getUserId().toString());
                    System.out.println(" User deleted from Monitoring DB");
                    break;
            }
        } catch (Exception e) {
            System.err.println(" Failed to process user sync: " + e.getMessage());
        }
    }
}