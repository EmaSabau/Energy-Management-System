package com.example.demo.dto;

import com.example.demo.dto.OverconsumptionEvent;
import java.time.LocalDateTime;

public class NotificationMessage {

    private String type;
    private String title;
    private String message;
    private String deviceId;
    private String deviceName;
    private double currentValue;
    private double maxValue;
    private LocalDateTime timestamp;

    public NotificationMessage() {}

    public NotificationMessage(String type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static NotificationMessage fromOverconsumption(OverconsumptionEvent event) {
        NotificationMessage notification = new NotificationMessage();
        notification.setType("overconsumption");
        notification.setTitle(" Alertă Supraconsum");

        String msg = String.format(
                "Dispozitivul '%s' a depășit limita maximă de consum! Curent: %.2f kWh, Maxim: %.2f kWh",
                event.getDeviceName(),
                event.getCurrentConsumption(),
                event.getMaxConsumption()
        );

        notification.setMessage(msg);
        notification.setDeviceId(event.getDeviceId() != null ? event.getDeviceId().toString() : null);
        notification.setDeviceName(event.getDeviceName());
        notification.setCurrentValue(event.getCurrentConsumption());
        notification.setMaxValue(event.getMaxConsumption());
        notification.setTimestamp(event.getTimestamp() != null ? event.getTimestamp() : java.time.LocalDateTime.now());

        return notification;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public double getMaxValue() { return maxValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}