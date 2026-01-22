package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dto.AppUserSyncDTO;
import com.example.demo.entities.AuthUser;
import com.example.demo.entities.Role;
import com.example.demo.repository.AuthUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Component
public class UserSyncListener {
    private static final Logger log = LoggerFactory.getLogger(UserSyncListener.class);
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSyncListener(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @RabbitListener(queues = RabbitConfig.AUTH_SERVICE_SYNC_QUEUE)
    @Transactional
    public void handleUserSync(AppUserSyncDTO dto) {
        log.info(" [Auth Service] Primit: {} pentru {}", dto.getAction(), dto.getUsername());
        try {
            switch (dto.getAction()) {
                case CREATE:
                    handleCreate(dto);
                    break;
                case UPDATE:
                    handleUpdate(dto);
                    break;
                case DELETE:
                    handleDelete(dto);
                    break;
            }
        } catch (Exception e) {
            log.error(" Eroare la procesare sincronizare Auth: {}", e.getMessage());
        }
    }

    private void handleCreate(AppUserSyncDTO dto) {
        if (authUserRepository.findByUsername(dto.getUsername()).isPresent()) return;
        AuthUser user = new AuthUser();
        user.setId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "1234"));
        user.setRole(Set.of(Role.ROLE_CLIENT));
        authUserRepository.save(user);
        log.info(" User sincronizat în Auth-DB: {}", dto.getUsername());
    }

    private void handleUpdate(AppUserSyncDTO dto) {
        authUserRepository.findById(dto.getUserId()).ifPresent(user -> {
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            authUserRepository.save(user);
            log.info(" User actualizat în Auth-DB: {}", dto.getUsername());
        });
    }

    private void handleDelete(AppUserSyncDTO dto) {
        authUserRepository.findById(dto.getUserId()).ifPresent(user -> {
            authUserRepository.delete(user);
            log.info(" User șters din Auth-DB: {}", dto.getUsername());
        });
    }
}