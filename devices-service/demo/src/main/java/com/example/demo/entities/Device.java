package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, unique = true)
    private String address;

    private int maxConsumption;

    private String imageUrl;

    @Column
    private UUID ownerID;

    public Device() {}

    // Getters și Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public UUID getOwnerId() { return ownerID; }
    public void setOwnerId(UUID ownerID) { this.ownerID = ownerID; }
}
