package com.example.demo.dto;

import java.util.UUID;

public class AppUserSyncDTO {

    public enum SyncAction {
        CREATE, UPDATE, DELETE
    }

    private SyncAction action;
    private UUID userId;
    private String username;
    private String oldUsername;
    private String email;
    private String role;
    private String password;

    public AppUserSyncDTO() {}
    
    public static AppUserSyncDTO createUser(String username, String email, String role, String password) {
        AppUserSyncDTO dto = new AppUserSyncDTO();
        dto.setAction(SyncAction.CREATE);
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setPassword(password);
        return dto;
    }

    public static AppUserSyncDTO updateUser(UUID userId, String oldUsername, String newUsername, String email, String role) {
        AppUserSyncDTO dto = new AppUserSyncDTO();
        dto.setAction(SyncAction.UPDATE);
        dto.setUserId(userId);
        dto.setOldUsername(oldUsername);
        dto.setUsername(newUsername);
        dto.setEmail(email);
        dto.setRole(role);
        return dto;
    }

    public static AppUserSyncDTO deleteUser(UUID userId, String username) {
        AppUserSyncDTO dto = new AppUserSyncDTO();
        dto.setAction(SyncAction.DELETE);
        dto.setUserId(userId);
        dto.setUsername(username);
        return dto;
    }

    // Getters și Setters
    public SyncAction getAction() {
        return action;
    }

    public void setAction(SyncAction action) {
        this.action = action;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserSyncDTO{" +
                "action=" + action +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", oldUsername='" + oldUsername + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", password=" + (password != null ? "[PROTECTED]" : "null") +
                '}';
    }
}