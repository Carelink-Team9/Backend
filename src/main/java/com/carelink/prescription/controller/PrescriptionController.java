package com.carelink.prescription.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.prescription.dto.PrescriptionResponse;
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

    // 1. 처방전 이미지 업로드 및 AI 분석 실행
    @PostMapping("/upload")
    public ResponseEntity<Long> uploadPrescription(
            @CurrentUserId Long userId,
            @RequestParam("file") MultipartFile file) {

        Long prescriptionId = prescriptionService.uploadAndProcessPrescription(userId, file);
        return ResponseEntity.ok(prescriptionId);
    }

    // 2. 분석된 처방전 결과 조회
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionResponse> getPrescription(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId) {

        PrescriptionResponse response = prescriptionService.getPrescriptionDetails(userId, prescriptionId);
        return ResponseEntity.ok(response);
    }
}