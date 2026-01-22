package com.example.demo.dto;

public class HourlyConsumptionDTO {

    private int hour;
    private double value;

    public HourlyConsumptionDTO() {}

    public HourlyConsumptionDTO(int hour, double value) {
        this.hour = hour;
        this.value = value;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}