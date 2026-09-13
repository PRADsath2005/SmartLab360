package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.Equipment;
import com.smartlab360.backend.entity.MaintenanceRequest;
import com.smartlab360.backend.entity.User;
import com.smartlab360.backend.repository.EquipmentRepository;
import com.smartlab360.backend.repository.MaintenanceRepository;
import com.smartlab360.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MaintenanceService(
            MaintenanceRepository maintenanceRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }


    public MaintenanceRequest createRequest(
            Long equipmentId,
            Long userId,
            MaintenanceRequest request) {

        Equipment equipment =
                equipmentRepository.findById(equipmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Equipment not found"));

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        request.setEquipment(equipment);
        request.setReportedBy(user);

        if (request.getStatus() == null ||
                request.getStatus().isBlank()) {

            request.setStatus("PENDING");
        }

        return maintenanceRepository.save(request);
    }


    public List<MaintenanceRequest> getAllRequests() {

        return maintenanceRepository.findAll();
    }


    public List<MaintenanceRequest> getRequestsByEquipment(
            Long equipmentId) {

        return maintenanceRepository
                .findByEquipmentId(equipmentId);
    }


    public List<MaintenanceRequest> getRequestsByUser(
            Long userId) {

        return maintenanceRepository
                .findByReportedById(userId);
    }


    public MaintenanceRequest getRequestById(
            Long id) {

        return maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Maintenance request not found"));
    }


    public MaintenanceRequest updateStatus(
            Long id,
            String status) {

        MaintenanceRequest request =
                getRequestById(id);


        String newStatus =
                status.toUpperCase();


        request.setStatus(newStatus);


        MaintenanceRequest updatedRequest =
                maintenanceRepository.save(request);


        /*
         * =================================
         * AUTOMATIC NOTIFICATION
         * =================================
         */

        String equipmentName =
                request.getEquipment() != null
                        ? request.getEquipment().getName()
                        : "Equipment";


        Long userId =
                request.getReportedBy().getId();


        if ("IN_PROGRESS".equals(newStatus)) {

            notificationService.createNotification(

                    userId,

                    "Maintenance #" +
                    request.getId() +
                    " for " +
                    equipmentName +
                    " is now in progress."

            );

        }


        else if ("COMPLETED".equals(newStatus)) {

            notificationService.createNotification(

                    userId,

                    "Maintenance #" +
                    request.getId() +
                    " for " +
                    equipmentName +
                    " has been completed."

            );

        }


        return updatedRequest;
    }


    public void deleteRequest(
            Long id) {

        if (!maintenanceRepository
                .existsById(id)) {

            throw new RuntimeException(
                    "Maintenance request not found");
        }

        maintenanceRepository.deleteById(id);
    }
}