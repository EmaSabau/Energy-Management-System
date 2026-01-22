package com.example.demo.services;

import com.example.demo.dtos.AppUserSyncDTO;
import com.example.demo.entities.AppUser;
import com.example.demo.repositories.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppUserService {

    private final AppUserRepository userRepository;
    private final UserSyncPublisher userSyncPublisher;

    public AppUserService(AppUserRepository userRepository, UserSyncPublisher userSyncPublisher) {
        this.userRepository = userRepository;
        this.userSyncPublisher = userSyncPublisher;
    }

    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public AppUser createUser(AppUserSyncDTO dto) {
        AppUser user = new AppUser();
        user.setId(dto.getUserId() != null ? dto.getUserId() : UUID.randomUUID());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setActive(true);

        AppUser saved = userRepository.save(user);
        userSyncPublisher.sendUserCreated(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole(), dto.getPassword());
        return saved;
    }

    public AppUser updateUser(UUID id, AppUserSyncDTO dto) {
        AppUser user = userRepository.findById(id).orElseThrow();
        String oldUsername = user.getUsername();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        AppUser updated = userRepository.save(user);
        userSyncPublisher.sendUserUpdated(updated.getId(), oldUsername, updated.getUsername(), updated.getEmail(), updated.getRole());
        return updated;
    }

    public void deleteUser(UUID id) {
        AppUser user = userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        userSyncPublisher.sendUserDeleted(id, user.getUsername());
    }

    public List<AppUser> getAllUsers() { return userRepository.findAll(); }
}