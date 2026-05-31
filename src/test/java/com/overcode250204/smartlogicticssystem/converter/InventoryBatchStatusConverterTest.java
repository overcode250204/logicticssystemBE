package com.overcode250204.smartlogicticssystem.converter;

import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryBatchStatusConverterTest {

    private final InventoryBatchStatusConverter converter = new InventoryBatchStatusConverter();

    @Test
    void convertToEntityAttributeMapsNullAndBlankToGood() {
        assertEquals(InventoryBatchStatus.GOOD, converter.convertToEntityAttribute(null));
        assertEquals(InventoryBatchStatus.GOOD, converter.convertToEntityAttribute(" "));
    }

    @Test
    void convertToEntityAttributeMapsValidEnumValues() {
        assertEquals(InventoryBatchStatus.OUT_OF_STOCK, converter.convertToEntityAttribute("OUT_OF_STOCK"));
        assertEquals(InventoryBatchStatus.EXPIRING_SOON, converter.convertToEntityAttribute("EXPIRING_SOON"));
        assertEquals(InventoryBatchStatus.LOW_STOCK, converter.convertToEntityAttribute("LOW_STOCK"));
        assertEquals(InventoryBatchStatus.NORMAL, converter.convertToEntityAttribute("NORMAL"));
    }

    @Test
    void convertToEntityAttributeMapsLegacyValues() {
        assertEquals(InventoryBatchStatus.GOOD, converter.convertToEntityAttribute("Good"));
        assertEquals(InventoryBatchStatus.OUT_OF_STOCK, converter.convertToEntityAttribute("het hang"));
        assertEquals(InventoryBatchStatus.EXPIRING_SOON, converter.convertToEntityAttribute("sap het han"));
        assertEquals(InventoryBatchStatus.LOW_STOCK, converter.convertToEntityAttribute("ton kho thap"));
        assertEquals(InventoryBatchStatus.NORMAL, converter.convertToEntityAttribute("binh thuong"));
    }

    @Test
    void convertToEntityAttributeDefaultsUnexpectedValuesToGood() {
        assertEquals(InventoryBatchStatus.GOOD, converter.convertToEntityAttribute("damaged"));
    }
}
