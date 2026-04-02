package com.carelink.prescription.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.prescription.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadPrescription(
            @CurrentUserId Long userId,
            @RequestParam("file") MultipartFile file) {

        Long prescriptionId = prescriptionService.uploadAndProcessPrescription(userId, file);
        return ResponseEntity.ok(prescriptionId);
    }
}