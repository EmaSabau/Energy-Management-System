package com.example.demo.dtos;

import java.util.UUID;

public class DeviceDTO {
    private String name;
    private String description;
    private String address;
    private int maxConsumption;
    private String imageUrl;
    private UUID ownerId;

    public DeviceDTO() {}

    public DeviceDTO(String string, String name, int maxConsumption) {
    }

    // Getters și Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(int maxConsumption) { this.maxConsumption = maxConsumption; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
}
