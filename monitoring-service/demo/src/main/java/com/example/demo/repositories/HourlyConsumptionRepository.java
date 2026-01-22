package com.example.demo.repositories;

import com.example.demo.entities.HourlyConsumption;
import com.example.demo.entities.HourlyConsumptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, HourlyConsumptionId> {
    Optional<HourlyConsumption> findById_DeviceIdAndId_HourStart(UUID deviceId, OffsetDateTime hourStart);

    @Query("SELECT h FROM HourlyConsumption h WHERE h.id.deviceId = :deviceId " +
            "AND h.id.hourStart >= :start AND h.id.hourStart < :end")
    List<HourlyConsumption> findByDeviceIdAndDateRange(
            @Param("deviceId") UUID deviceId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);
}