package com.example.demo.services;

import com.example.demo.dtos.OwnerUsernameUpdateDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeviceSyncService {

    private final RestTemplate restTemplate;

    public DeviceSyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void syncUpdateOwnerUsername(String oldUsername, String newUsername) {
        String url = "http://device-service:8080/api/devices/sync-owner";
        OwnerUsernameUpdateDTO dto = new OwnerUsernameUpdateDTO();
        dto.setOldUsername(oldUsername);
        dto.setNewUsername(newUsername);

        restTemplate.put(url, dto);
    }

    public void syncUnassignUserDevices(String username) {
        String url = "http://device-service:8080/api/devices/unassign-user/" + username;
        restTemplate.put(url, null); // nu trimitem body
    }
}
