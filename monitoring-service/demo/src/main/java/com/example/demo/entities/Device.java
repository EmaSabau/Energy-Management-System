package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private UUID id;

    @Column(nullable = true)
    private String name;

    @Column(name = "max_consumption")
    private Integer maxConsumption;  // în Watts

    @Column(name = "owner_id")
    private UUID ownerId;

    public Device() {}

    public Device(UUID id) {
        this.id = id;
    }

    public Device(UUID id, String name, Integer maxConsumption, UUID ownerId) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Integer maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}