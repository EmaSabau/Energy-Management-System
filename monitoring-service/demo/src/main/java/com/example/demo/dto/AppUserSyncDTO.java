package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 🚀 Adăugat
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true) // 🛡️ REPARAT: Ignoră câmpurile trimise de User Service dar nefolosite aici
public class AppUserSyncDTO implements Serializable {
    private UUID userId;
    private String username;
    private SyncAction action;

    public enum SyncAction { CREATE, UPDATE, DELETE }

    public AppUserSyncDTO() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public SyncAction getAction() { return action; }
    public void setAction(SyncAction action) { this.action = action; }
}