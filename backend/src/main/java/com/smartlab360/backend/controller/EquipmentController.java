package com.smartlab360.backend.controller;

import com.smartlab360.backend.entity.Equipment;
import com.smartlab360.backend.service.EquipmentService;
import com.smartlab360.backend.service.QRCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final QRCodeService qrCodeService;

    public EquipmentController(
            EquipmentService equipmentService,
            QRCodeService qrCodeService) {

        this.equipmentService = equipmentService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {

        return ResponseEntity.ok(
                equipmentService.getAllEquipment()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquipmentById(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    equipmentService.getEquipmentById(id)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Equipment> addEquipment(
            @Valid @RequestBody Equipment equipment) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        equipmentService.addEquipment(equipment)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody Equipment equipment) {

        try {

            return ResponseEntity.ok(
                    equipmentService.updateEquipment(
                            id,
                            equipment
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(
            @PathVariable Long id) {

        try {

            equipmentService.getEquipmentById(id);

            equipmentService.deleteEquipment(id);

            return ResponseEntity.ok(
                    "Equipment deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // QR CODE

    @GetMapping(
            value = "/{id}/qrcode",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable Long id) {

        Equipment equipment =
                equipmentService.getEquipmentById(id);

        String qrData =
                "http://10.16.120.227:8080/equipment-details.html?id="
                        + equipment.getId();

        byte[] qrCode =
                qrCodeService.generateQRCode(
                        qrData,
                        300,
                        300
                );

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}
