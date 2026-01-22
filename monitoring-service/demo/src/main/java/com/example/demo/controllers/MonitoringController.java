package com.example.demo.controllers;

import com.example.demo.dto.HourlyConsumptionDTO;
import com.example.demo.services.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final MonitoringService service;

    public MonitoringController(MonitoringService service) {
        this.service = service;
    }

    @GetMapping("/device/{deviceId}/daily")
    public ResponseEntity<List<HourlyConsumptionDTO>> getDailyConsumption(
            @PathVariable String deviceId,
            @RequestParam String date
    ) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<HourlyConsumptionDTO> consumption = service.getDailyConsumption(deviceId, parsedDate);
            return ResponseEntity.ok(consumption);
        } catch (Exception e) {
            System.err.println("Error getting daily consumption: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/device/{deviceId}/health")
    public ResponseEntity<String> checkDeviceHealth(@PathVariable String deviceId) {
        return ResponseEntity.ok("Device " + deviceId + " is being monitored");
    }
}