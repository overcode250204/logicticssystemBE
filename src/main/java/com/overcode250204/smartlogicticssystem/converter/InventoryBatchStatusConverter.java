package com.overcode250204.smartlogicticssystem.converter;

import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InventoryBatchStatusConverter implements AttributeConverter<InventoryBatchStatus, String> {

    @Override
    public String convertToDatabaseColumn(InventoryBatchStatus status) {
        return status == null ? null : status.name();
    }

    @Override
    public InventoryBatchStatus convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return InventoryBatchStatus.GOOD;
        }

        String normalizedValue = value.trim().toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');

        try {
            return InventoryBatchStatus.valueOf(normalizedValue);
        } catch (IllegalArgumentException e) {
            return mapLegacyStatus(normalizedValue);
        }
    }

    private InventoryBatchStatus mapLegacyStatus(String value) {
        return switch (value) {
            case "HET_HANG", "OUT", "OUT_OF_STOCK" -> InventoryBatchStatus.OUT_OF_STOCK;
            case "SAP_HET_HAN", "EXPIRING", "EXPIRING_SOON" -> InventoryBatchStatus.EXPIRING_SOON;
            case "TON_KHO_THAP", "LOW", "LOW_STOCK" -> InventoryBatchStatus.LOW_STOCK;
            case "BINH_THUONG", "NORMAL" -> InventoryBatchStatus.NORMAL;
            default -> InventoryBatchStatus.GOOD;
        };
    }
}
