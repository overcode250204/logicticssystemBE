package com.overcode250204.smartlogicticssystem.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BarcodeGeneratorUtilTest {

    @Test
    void generateEAN13BarcodeCalculatesChecksumAndPngImage() {
        BarcodeGeneratorUtil.GeneratedBarcode barcode = BarcodeGeneratorUtil.generateEAN13Barcode("590123412345");

        assertEquals("5901234123457", barcode.barcode());
        assertTrue(barcode.pngBytes().length > 0);
        assertArrayEquals(new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}, firstFourBytes(barcode.pngBytes()));
    }

    @Test
    void generateEAN13BarcodeRejectsNonNumericInput() {
        assertThrows(IllegalArgumentException.class,
                () -> BarcodeGeneratorUtil.generateEAN13Barcode("590123ABC345"));
    }

    @Test
    void generateEAN13BarcodeRejectsWrongLengthInput() {
        assertThrows(IllegalArgumentException.class,
                () -> BarcodeGeneratorUtil.generateEAN13Barcode("59012341234"));
    }

    private byte[] firstFourBytes(byte[] bytes) {
        return new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
    }
}
