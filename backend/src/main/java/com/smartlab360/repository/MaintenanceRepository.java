package com.smartlab360.backend.repository;

import com.smartlab360.backend.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository
        extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByEquipmentId(Long equipmentId);

    List<MaintenanceRequest> findByReportedById(Long userId);

    List<MaintenanceRequest> findByStatus(String status);
}