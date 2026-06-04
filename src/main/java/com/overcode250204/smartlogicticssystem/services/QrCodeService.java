package com.overcode250204.smartlogicticssystem.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.StorageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private static final int QR_SIZE = 300;
    private static final String QR_FOLDER = "qr-codes";
    private static final String PNG_CONTENT_TYPE = "image/png";

    private final S3FileService s3FileService;

    public String generateAndUploadQrCode(String barcode) {
        byte[] qrBytes = generateQrPngBytes(barcode);
        String key = "%s/%s.png".formatted(QR_FOLDER, barcode);
        return s3FileService.uploadBytes(qrBytes, key, PNG_CONTENT_TYPE);
    }

    private byte[] generateQrPngBytes(String barcode) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(barcode, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new AppException(StorageErrorCode.QR_CODE_GENERATION_FAILED);
        }
    }
}
