package com.smartlab360.backend.controller;

import com.smartlab360.backend.entity.MaintenanceRequest;
import com.smartlab360.backend.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(
            MaintenanceService maintenanceService) {

        this.maintenanceService = maintenanceService;
    }

    // Create maintenance request
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestParam Long equipmentId,
            @RequestParam Long userId,
            @Valid @RequestBody MaintenanceRequest request) {

        try {

            MaintenanceRequest savedRequest =
                    maintenanceService.createRequest(
                            equipmentId,
                            userId,
                            request
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedRequest);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // Get all maintenance requests
    @GetMapping
    public ResponseEntity<List<MaintenanceRequest>>
    getAllRequests() {

        return ResponseEntity.ok(
                maintenanceService.getAllRequests()
        );
    }

    // Get requests by equipment
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<MaintenanceRequest>>
    getRequestsByEquipment(
            @PathVariable Long equipmentId) {

        return ResponseEntity.ok(
                maintenanceService
                        .getRequestsByEquipment(equipmentId)
        );
    }

    // Get requests by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MaintenanceRequest>>
    getRequestsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                maintenanceService
                        .getRequestsByUser(userId)
        );
    }

    // Get request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    maintenanceService
                            .getRequestById(id)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // Update maintenance status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            MaintenanceRequest updatedRequest =
                    maintenanceService.updateStatus(
                            id,
                            status
                    );

            return ResponseEntity.ok(
                    updatedRequest
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // Delete maintenance request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(
            @PathVariable Long id) {

        try {

            maintenanceService.deleteRequest(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Maintenance request deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}