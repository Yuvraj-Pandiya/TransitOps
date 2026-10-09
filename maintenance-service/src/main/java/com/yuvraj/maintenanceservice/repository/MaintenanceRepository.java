package com.yuvraj.maintenanceservice.repository;

import com.yuvraj.maintenanceservice.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    Optional<List<Maintenance>> findMaintenanceByVehicleId(Long id);
}
