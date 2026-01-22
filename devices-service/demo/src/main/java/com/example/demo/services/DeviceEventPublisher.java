package com.example.demo.services;

import com.example.demo.config.DeviceRabbitConfig;
import com.example.demo.dtos.DeviceSyncEvent;
import com.example.demo.entities.Device;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeviceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public DeviceEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishDeviceCreated(Device device) {
        DeviceSyncEvent event = new DeviceSyncEvent(
                "device_created",
                device.getId().toString(),
                device.getName(),
                device.getMaxConsumption(),
                device.getOwnerId() != null ? device.getOwnerId().toString() : null
        );
        rabbitTemplate.convertAndSend(
                DeviceRabbitConfig.DEVICE_EXCHANGE,
                "device.create",
                event
        );
        System.out.println(" Published device.create event for: " + device.getId());
    }

    public void publishDeviceUpdated(Device device) {
        DeviceSyncEvent event = new DeviceSyncEvent(
                "device_updated",
                device.getId().toString(),
                device.getName(),
                device.getMaxConsumption(),
                device.getOwnerId() != null ? device.getOwnerId().toString() : null
        );

        rabbitTemplate.convertAndSend(
                DeviceRabbitConfig.DEVICE_EXCHANGE,
                "device.update",
                event
        );
        System.out.println(" Published device.update event for: " + device.getId());
    }

    public void publishDeviceDeleted(String deviceId) {
        DeviceSyncEvent event = new DeviceSyncEvent("device_deleted", deviceId);
        rabbitTemplate.convertAndSend(
                DeviceRabbitConfig.DEVICE_EXCHANGE,
                "device.delete",
                event
        );
        System.out.println(" Published device.delete event for: " + deviceId);
    }
}