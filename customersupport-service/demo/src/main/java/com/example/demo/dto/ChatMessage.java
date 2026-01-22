package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ChatMessage {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("username")
    private String username;

    private String message;

    private LocalDateTime timestamp;
    private boolean adminRequest;


    public ChatMessage() {
        this.timestamp = LocalDateTime.now();

    }

    public ChatMessage(String userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isAdminRequest() { return adminRequest; }
    public void setAdminRequest(boolean adminRequest) { this.adminRequest = adminRequest; }
}
