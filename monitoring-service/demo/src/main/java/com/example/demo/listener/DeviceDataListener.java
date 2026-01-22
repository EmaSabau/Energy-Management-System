package com.example.demo.listener;

import com.example.demo.dto.DeviceMeasurement;
import com.example.demo.services.MonitoringService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceDataListener {

    private final MonitoringService monitoringService;

    public DeviceDataListener(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @RabbitListener(queues = "device_data_queue")
    public void receiveMessage(DeviceMeasurement measurement) {
        monitoringService.processMeasurement(measurement);
    }
}