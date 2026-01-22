package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatResponse;
import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatRelayController {

    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRelayController(RabbitTemplate rabbitTemplate, SimpMessagingTemplate messagingTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void relayToSupport(ChatMessage message) {
        if (message.isAdminRequest()) {
            messagingTemplate.convertAndSend("/topic/admin.support", message);
        } else {
            rabbitTemplate.convertAndSend(RabbitConfig.CHAT_REQUEST_QUEUE, message);
        }
    }

    @MessageMapping("/chat.admin.reply")
    public void adminReply(ChatResponse response) {
        messagingTemplate.convertAndSendToUser(
                response.getSender(),
                "/queue/chat",
                response
        );
    }
}