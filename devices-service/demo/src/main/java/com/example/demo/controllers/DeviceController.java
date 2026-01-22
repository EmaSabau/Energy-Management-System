package com.example.demo.controllers;

import com.example.demo.entities.Device;
import com.example.demo.services.DeviceService;
// ❌ ȘTERGE: import com.example.demo.services.SimulatorPublisher;
// NU MAI TREBUIE SimulatorPublisher - logica de publishing e în DeviceService!

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * GET /api/devices - toate device-urile
     */
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    /**
     * GET /api/devices/user/{userId} - device-urile unui user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Device>> getDevicesByUser(@PathVariable UUID userId) {
        List<Device> devices = deviceService.getDevicesByOwnerId(userId);
        return ResponseEntity.ok(devices);
    }

    /**
     * GET /api/devices/{id} - un device specific
     */
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable UUID id) {
        return deviceService.getDeviceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/devices - creează device nou
     * DeviceService va publica automat eveniment de sincronizare
     */
    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        try {
            Device created = deviceService.createDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            System.err.println("Error creating device: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/devices/{id} - actualizează device
     */
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(
            @PathVariable UUID id,
            @RequestBody Device deviceDetails) {
        try {
            Device updated = deviceService.updateDevice(id, deviceDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error updating device: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /api/devices/{id} - șterge device
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error deleting device: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/devices/{deviceId}/assign/{userId} - asignează device la user
     * (dacă ai această funcționalitate)
     */
    @PostMapping("/{deviceId}/assign/{userId}")
    public ResponseEntity<Device> assignDeviceToUser(
            @PathVariable UUID deviceId,
            @PathVariable UUID userId) {
        try {
            Device device = deviceService.getDeviceById(deviceId)
                    .orElseThrow(() -> new RuntimeException("Device not found"));

            device.setOwnerId(userId);
            Device updated = deviceService.updateDevice(deviceId, device);

            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/devices/{deviceId}/unassign - dezasignează device de la user
     */
    @PostMapping("/{deviceId}/unassign")
    public ResponseEntity<Device> unassignDevice(@PathVariable UUID deviceId) {
        try {
            Device device = deviceService.getDeviceById(deviceId)
                    .orElseThrow(() -> new RuntimeException("Device not found"));

            device.setOwnerId(null);
            Device updated = deviceService.updateDevice(deviceId, device);

            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}