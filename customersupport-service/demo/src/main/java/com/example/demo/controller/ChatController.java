package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatResponse;
import com.example.demo.config.RabbitConfig;
import com.example.demo.service.AICustomerSupportService;
import com.example.demo.service.ChatbotService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatController {

    private final ChatbotService chatbotService;
    private final AICustomerSupportService aiSupportService;
    private final RabbitTemplate rabbitTemplate;

    public ChatController(ChatbotService chatbotService,
                          AICustomerSupportService aiSupportService,
                          RabbitTemplate rabbitTemplate) {
        this.chatbotService = chatbotService;
        this.aiSupportService = aiSupportService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.CHAT_REQUEST_QUEUE)
    public void handleChatRequest(ChatMessage message) {
        System.out.println(" Procesare cerere chat de la: " + message.getUsername());
        String localResponse = chatbotService.processMessage(message.getMessage());
        ChatResponse response;

        if (localResponse != null && !localResponse.contains("nu am un răspuns")) {
            response = ChatResponse.fromRule(localResponse);
        } else {
            System.out.println(" Niciun match local. Se apelează Gemini AI...");
            try {
                String aiText = aiSupportService.getAIResponse(message.getMessage());
                response = ChatResponse.fromAI(aiText);
            } catch (Exception e) {
                System.err.println(" Eroare Gemini API: " + e.getMessage());
                response = ChatResponse.fromRule("Îmi pare rău, asistentul AI întâmpină dificultăți. Te rog să încerci mai târziu.");
            }
        }
        response.setSender(message.getUsername());

        rabbitTemplate.convertAndSend(RabbitConfig.CHAT_RESPONSE_QUEUE, response);
        System.out.println(" Răspuns trimis pentru: " + message.getUsername() +
                " (Sursă: " + (response.isAiGenerated() ? "AI" : "Rule") + ")");
    }
}