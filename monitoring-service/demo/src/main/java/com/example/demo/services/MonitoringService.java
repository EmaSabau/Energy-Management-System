package com.example.demo.services;

import com.example.demo.dto.DeviceMeasurement;
import com.example.demo.dto.HourlyConsumptionDTO;
import com.example.demo.dto.OverconsumptionEvent;
import com.example.demo.entities.Device;
import com.example.demo.entities.HourlyConsumption;
import com.example.demo.entities.HourlyConsumptionId;
import com.example.demo.entities.User;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.repositories.HourlyConsumptionRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class MonitoringService {

    private final HourlyConsumptionRepository repository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public MonitoringService(HourlyConsumptionRepository repository,
                             DeviceRepository deviceRepository, UserRepository userRepository,
                             RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void processMeasurement(DeviceMeasurement measurement) {
        try {
            UUID deviceUuid = UUID.fromString(measurement.deviceId);
            LocalDateTime ldt = measurement.getParsedTimestamp();
            OffsetDateTime hourStart = ldt.withMinute(0).withSecond(0).withNano(0).atOffset(ZoneOffset.UTC);
            HourlyConsumptionId id = new HourlyConsumptionId(deviceUuid, hourStart);
            HourlyConsumption hc = repository.findById(id)
                    .orElse(new HourlyConsumption(id, 0.0));
            double newTotal = hc.getTotalKwh() + measurement.measurementValue;
            hc.setTotalKwh(newTotal);
            repository.save(hc);
            checkOverconsumption(deviceUuid, newTotal);

        } catch (IllegalArgumentException e) {
            System.err.println("Eroare: ID-ul dispozitivului nu este un UUID valid: " + measurement.deviceId);
        }
    }

    private void checkOverconsumption(UUID deviceId, double hourlyTotal) {
        deviceRepository.findById(deviceId).ifPresent(device -> {
            if (device.getMaxConsumption() != null) {
                double limitKwh = device.getMaxConsumption() / 1000.0;

                if (hourlyTotal > limitKwh) {
                    String ownerIdStr = device.getOwnerId().toString();
                    String username = userRepository.findById(ownerIdStr)
                            .map(User::getUsername)
                            .orElse("Unknown User");

                    OverconsumptionEvent event = new OverconsumptionEvent();
                    event.setDeviceId(device.getId());
                    event.setDeviceName(device.getName());
                    event.setUsername(username);
                    event.setCurrentConsumption(hourlyTotal);
                    event.setMaxConsumption(limitKwh);
                    event.setTimestamp(LocalDateTime.now());
                    rabbitTemplate.convertAndSend(
                            "overconsumption.exchange",
                            "overconsumption",
                            event
                    );

                    System.out.println("Alertă trimisă către RabbitMQ pentru utilizatorul: " + username);
                }
            }
        });
    }

    public List<HourlyConsumptionDTO> getDailyConsumption(String deviceIdStr, LocalDate date) {
        UUID deviceId = UUID.fromString(deviceIdStr);
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = startOfDay.plusDays(1);

        List<HourlyConsumption> hourlyData = repository.findByDeviceIdAndDateRange(
                deviceId, startOfDay, endOfDay);

        List<HourlyConsumptionDTO> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            final int currentHour = hour;
            double value = hourlyData.stream()
                    .filter(hc -> hc.getId().getHourStart().getHour() == currentHour)
                    .mapToDouble(HourlyConsumption::getTotalKwh)
                    .sum();

            result.add(new HourlyConsumptionDTO(currentHour, value));
        }
        return result;
    }
}