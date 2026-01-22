package com.example.demo.listener;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dto.DeviceSyncEvent;
import com.example.demo.entities.Device;
import com.example.demo.repositories.DeviceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class DeviceSyncListener {

    private final DeviceRepository deviceRepository;

    public DeviceSyncListener(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @RabbitListener(queues = RabbitConfig.MONITORING_DEVICE_SYNC_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onDeviceSyncEvent(DeviceSyncEvent event) {
        try {
            System.out.println(" Received device sync: " + event.getDeviceId());
            switch (event.getEventType()) {
                case "device_created":
                case "device_updated":
                    Device device = new Device();
                    device.setId(UUID.fromString(event.getDeviceId()));
                    device.setName(event.getName());
                    device.setMaxConsumption(event.getMaxConsumption());
                    if (event.getOwnerId() != null && !event.getOwnerId().isEmpty()) {
                        device.setOwnerId(UUID.fromString(event.getOwnerId()));
                    }
                    deviceRepository.save(device);
                    System.out.println(" Device synced in local Monitoring DB");
                    break;
                case "device_deleted":
                    deviceRepository.deleteById(UUID.fromString(event.getDeviceId()));
                    System.out.println(" Device deleted from Monitoring DB");
                    break;
            }
        } catch (Exception e) {
            System.err.println(" Failed to sync device: " + e.getMessage());
        }
    }
}