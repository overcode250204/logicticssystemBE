package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.overcode250204.smartlogicticssystem.dtos.request.FailPointRequestDTO;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/driver/local-trips")
@RequiredArgsConstructor
public class LocalTripDriverController extends BaseController {

    private final ILocalTripService localTripService;
    private final S3FileService s3FileService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<LocalTripResponseDTO>>> getDriverLocalTrips(@RequestHeader("driverId") Long driverId) {
        return success(localTripService.getLocalTripsByDriverId(driverId), "Driver local trips retrieved successfully");
    }

    @PutMapping("/{tripId}/accept")
    public ResponseEntity<BaseResponse<Void>> acceptTrip(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.acceptTrip(driverId, tripId);
        return success(null, "Trip accepted successfully");
    }

    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<BaseResponse<Void>> cancelTrip(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.cancelTrip(driverId, tripId);
        return success(null, "Trip cancelled successfully");
    }

    @PostMapping("/{tripId}/scan-barcode")
    public ResponseEntity<BaseResponse<Void>> scanBarcode(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId, @RequestParam Long orderId, @RequestParam String barcode) {
        localTripService.scanBarcode(driverId, tripId, orderId, barcode);
        return success(null, "Barcode scanned successfully");
    }

    @PutMapping("/{tripId}/start-executing")
    public ResponseEntity<BaseResponse<Void>> startExecuting(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.startExecuting(driverId, tripId);
        return success(null, "Trip started successfully");
    }

    @PutMapping("/{tripId}/details/{detailId}/arrive")
    public ResponseEntity<BaseResponse<Void>> arriveAtPoint(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId, @PathVariable Long detailId,
                                              @RequestParam double lat, @RequestParam double lon) {
        localTripService.arriveAtPoint(driverId, tripId, detailId, lat, lon);
        return success(null, "Arrived at point successfully");
    }

    @PutMapping(value = "/{tripId}/details/{detailId}/complete", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Void>> completePoint(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId, @PathVariable Long detailId,
                                              @RequestPart("proofImage") MultipartFile proofImage) {
        String proofUrl = s3FileService.uploadFile(proofImage, "proofs");
        localTripService.completePoint(driverId, tripId, detailId, proofUrl);
        return success(null, "Point completed successfully");
    }

    @PutMapping(value = "/{tripId}/details/{detailId}/fail", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Void>> failPoint(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId, @PathVariable Long detailId,
                                          @RequestPart("proofImage") MultipartFile proofImage,
                                          @RequestPart("data") @Valid FailPointRequestDTO data) {
        String proofUrl = s3FileService.uploadFile(proofImage, "proofs");
        localTripService.failPoint(driverId, tripId, detailId, proofUrl, data);
        return success(null, "Point failed successfully");
    }
}
