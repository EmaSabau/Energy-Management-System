package com.example.demo.services;

import com.example.demo.entities.Device;
import com.example.demo.services.DeviceEventPublisher;
import com.example.demo.repositories.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceEventPublisher eventPublisher;

    public DeviceService(DeviceRepository deviceRepository, DeviceEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Device createDevice(Device device) {
        Device savedDevice = deviceRepository.save(device);
        eventPublisher.publishDeviceCreated(savedDevice);

        return savedDevice;
    }

    @Transactional
    public Device updateDevice(UUID id, Device deviceDetails) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));

        device.setName(deviceDetails.getName());
        device.setDescription(deviceDetails.getDescription());
        device.setAddress(deviceDetails.getAddress());
        device.setMaxConsumption(deviceDetails.getMaxConsumption());
        device.setImageUrl(deviceDetails.getImageUrl());
        device.setOwnerId(deviceDetails.getOwnerId());

        Device updatedDevice = deviceRepository.save(device);
        eventPublisher.publishDeviceUpdated(updatedDevice);

        return updatedDevice;
    }

    @Transactional
    public void deleteDevice(UUID id) {
        if (!deviceRepository.existsById(id)) {
            throw new RuntimeException("Device not found with id: " + id);
        }

        deviceRepository.deleteById(id);
        eventPublisher.publishDeviceDeleted(id.toString());
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(UUID id) {
        return deviceRepository.findById(id);
    }

    public List<Device> getDevicesByOwnerId(UUID ownerId) {
        return deviceRepository.findByOwnerID(ownerId);
    }
}