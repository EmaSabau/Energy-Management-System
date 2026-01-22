package com.example.demo.listener;

import com.example.demo.dto.ChatResponse;
import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatResponseListener {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatResponseListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfig.CHAT_RESPONSE_QUEUE)
    public void handleResponseFromSupport(ChatResponse response) {
        messagingTemplate.convertAndSendToUser(
                response.getSender(),
                "/queue/chat",
                response
        );
        System.out.println("Delivered chat response via WebSocket to: " + response.getSender());
    }
}