package com.example.demo.services;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dtos.AppUserSyncDTO;
import com.example.demo.entities.AppUser;
import com.example.demo.repositories.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserSyncListener {

    private static final Logger log = LoggerFactory.getLogger(UserSyncListener.class);
    private final AppUserRepository appUserRepository;

    public UserSyncListener(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // 🚀 REPARAT: Ascultăm COADA, nu exchange-ul
    @RabbitListener(queues = RabbitConfig.USER_SERVICE_SYNC_QUEUE)
    @Transactional
    public void handleUserSync(AppUserSyncDTO dto) {
        log.info("📥 USER-SERVICE: Primit mesaj din {}", RabbitConfig.USER_SERVICE_SYNC_QUEUE);

        try {
            switch (dto.getAction()) {
                case CREATE:
                    handleUserCreate(dto);
                    break;
                case UPDATE:
                    handleUserUpdate(dto);
                    break;
                case DELETE:
                    handleUserDelete(dto);
                    break;
            }
        } catch (Exception e) {
            log.error("✗ Eroare la procesarea sincronizării", e);
        }
    }

    private void handleUserCreate(AppUserSyncDTO dto) {
        if (!appUserRepository.existsByUsername(dto.getUsername())) {
            AppUser user = new AppUser();
            user.setId(dto.getUserId()); // 🛡️ Setăm ID-ul primit
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setRole(dto.getRole());
            user.setActive(true);
            appUserRepository.save(user);
            log.info("✅ Creat user local: {}", dto.getUsername());
        }
    }

    private void handleUserUpdate(AppUserSyncDTO dto) {
        appUserRepository.findById(dto.getUserId()).ifPresent(user -> {
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setRole(dto.getRole());
            appUserRepository.save(user);
            log.info("✅ Actualizat user local: {}", dto.getUsername());
        });
    }

    private void handleUserDelete(AppUserSyncDTO dto) {
        appUserRepository.deleteById(dto.getUserId());
        log.info("✅ Șters user local ID: {}", dto.getUserId());
    }
}