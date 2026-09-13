package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.Equipment;
import com.smartlab360.backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    public Equipment addEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment equipment) {

        Equipment existing = getEquipmentById(id);

        existing.setName(equipment.getName());
        existing.setCategory(equipment.getCategory());
        existing.setDescription(equipment.getDescription());
        existing.setLocation(equipment.getLocation());
        existing.setStatus(equipment.getStatus());

        return equipmentRepository.save(existing);
    }

    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }
}