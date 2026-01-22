package com.example.demo.controller;

import com.example.demo.dto.NotificationMessage;
import com.example.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send/{username}")
    public ResponseEntity<String> sendNotification(
            @PathVariable String username,
            @RequestBody NotificationMessage notification) {
        notificationService.sendToUser(username, notification);
        return ResponseEntity.ok("Notification sent to user: " + username);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("WebSocket Service is running");
    }
}