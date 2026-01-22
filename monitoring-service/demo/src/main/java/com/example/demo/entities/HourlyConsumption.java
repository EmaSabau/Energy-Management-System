package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "hourly_consumption")
public class HourlyConsumption {

    @EmbeddedId
    private HourlyConsumptionId id;

    @Column(name = "total_kwh", nullable = false)
    private double totalKwh;

    public HourlyConsumption() {}

    public HourlyConsumption(HourlyConsumptionId id, double totalKwh) {
        this.id = id;
        this.totalKwh = totalKwh;
    }

    public HourlyConsumptionId getId() { return id; }
    public void setId(HourlyConsumptionId id) { this.id = id; }
    public double getTotalKwh() { return totalKwh; }
    public void setTotalKwh(double totalKwh) { this.totalKwh = totalKwh; }
}
