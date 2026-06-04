package com.overcode250204.smartlogicticssystem.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.StorageErrorCode;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public final class BarcodeGeneratorUtil {

    private static final int DATA_LENGTH = 12;
    private static final int BARCODE_LENGTH = 13;
    private static final int IMAGE_WIDTH = 420;
    private static final int BARCODE_HEIGHT = 140;
    private static final int TEXT_HEIGHT = 36;
    private static final int PADDING = 20;
    private static final String PNG_FORMAT = "PNG";

    private BarcodeGeneratorUtil() {
    }

    public static GeneratedBarcode generateEAN13Barcode(String data) {
        validateData(data);

        String barcode = data + calculateChecksum(data);
        return new GeneratedBarcode(barcode, renderBarcodePng(barcode));
    }

    private static void validateData(String data) {
        if (data == null || !data.matches("\\d{%d}".formatted(DATA_LENGTH))) {
            throw new IllegalArgumentException("EAN-13 barcode data must contain exactly 12 numeric digits");
        }
    }

    private static int calculateChecksum(String data) {
        int sum = 0;
        for (int i = 0; i < DATA_LENGTH; i++) {
            int digit = Character.digit(data.charAt(i), 10);
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }
        return (10 - (sum % 10)) % 10;
    }

    private static byte[] renderBarcodePng(String barcode) {
        try {
            BitMatrix bitMatrix = new EAN13Writer().encode(
                    barcode,
                    BarcodeFormat.EAN_13,
                    IMAGE_WIDTH - (PADDING * 2),
                    BARCODE_HEIGHT,
                    Map.of(EncodeHintType.MARGIN, 0)
            );

            int imageHeight = BARCODE_HEIGHT + TEXT_HEIGHT + (PADDING * 2);
            BufferedImage image = new BufferedImage(IMAGE_WIDTH, imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            try {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, IMAGE_WIDTH, imageHeight);
                graphics.setColor(Color.BLACK);

                int barcodeX = (IMAGE_WIDTH - bitMatrix.getWidth()) / 2;
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    for (int y = 0; y < bitMatrix.getHeight(); y++) {
                        if (bitMatrix.get(x, y)) {
                            graphics.fillRect(barcodeX + x, PADDING + y, 1, 1);
                        }
                    }
                }

                drawHumanReadableText(graphics, barcode, imageHeight);
            } finally {
                graphics.dispose();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, PNG_FORMAT, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AppException(StorageErrorCode.BARCODE_GENERATION_FAILED);
        }
    }

    private static void drawHumanReadableText(Graphics2D graphics, String barcode, int imageHeight) {
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));

        FontMetrics metrics = graphics.getFontMetrics();
        int textX = (IMAGE_WIDTH - metrics.stringWidth(barcode)) / 2;
        int textY = imageHeight - PADDING;
        graphics.drawString(barcode, textX, textY);
    }

    public record GeneratedBarcode(String barcode, byte[] pngBytes) {
        public GeneratedBarcode {
            if (barcode == null || !barcode.matches("\\d{%d}".formatted(BARCODE_LENGTH))) {
                throw new IllegalArgumentException("EAN-13 barcode must contain exactly 13 numeric digits");
            }
            if (pngBytes == null || pngBytes.length == 0) {
                throw new IllegalArgumentException("Barcode image bytes must not be empty");
            }
        }
    }
}
