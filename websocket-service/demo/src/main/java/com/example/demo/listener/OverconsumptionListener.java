package com.example.demo.listener;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dto.NotificationMessage;
import com.example.demo.dto.OverconsumptionEvent;
import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OverconsumptionListener {

    private static final Logger log = LoggerFactory.getLogger(OverconsumptionListener.class);
    private final NotificationService notificationService;

    public OverconsumptionListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(
            queues = RabbitConfig.OVERCONSUMPTION_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onOverconsumptionEvent(OverconsumptionEvent event) {
        try {
            log.info(" Eveniment de supraconsum primit pentru utilizatorul: {}", event.getUsername());
            NotificationMessage notification = NotificationMessage.fromOverconsumption(event);
            if (event.getUsername() != null && !event.getUsername().equalsIgnoreCase("Unknown")) {
                notificationService.sendToUser(event.getUsername(), notification);
            } else {
                log.warn(" Username-ul este 'Unknown'. Notificarea privată nu a putut fi trimisă.");
            }

        } catch (Exception e) {
            log.error(" Eroare la procesarea evenimentului de supraconsum: {}", e.getMessage());
        }
    }
}