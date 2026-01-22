package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ChatResponse {

    private String sender;
    private String message;

    @JsonProperty("rule_matched")
    private boolean ruleMatched;

    @JsonProperty("ai_generated")
    private boolean aiGenerated;

    private LocalDateTime timestamp;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatResponse(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static ChatResponse fromRule(String message) {
        ChatResponse response = new ChatResponse("bot", message);
        response.setRuleMatched(true);
        response.setAiGenerated(false);
        return response;
    }

    public static ChatResponse fromAI(String message) {
        ChatResponse response = new ChatResponse("ai", message);
        response.setRuleMatched(false);
        response.setAiGenerated(true);
        return response;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRuleMatched() { return ruleMatched; }
    public void setRuleMatched(boolean ruleMatched) { this.ruleMatched = ruleMatched; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}