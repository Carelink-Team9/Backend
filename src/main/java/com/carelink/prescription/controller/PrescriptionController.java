package com.carelink.prescription.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.prescription.dto.PrescriptionResponse;
import com.carelink.prescription.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription", description = "처방전 업로드 및 상세 조회 API")
@SecurityRequirement(name = "sessionCookieAuth")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // 1. 처방전 이미지 업로드 및 AI 분석 실행
    @Operation(summary = "처방전 이미지 업로드", description = "처방전 이미지를 업로드하고 AI 분석을 시작합니다.")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Long> uploadPrescription(
            @CurrentUserId Long userId,
            @RequestParam("file") MultipartFile file) {

        Long prescriptionId = prescriptionService.uploadAndProcessPrescription(userId, file);
        return ResponseEntity.ok(prescriptionId);
    }

    // 2. 분석된 처방전 결과 조회
    @Operation(summary = "처방전 상세 조회", description = "분석이 완료된 처방전 결과를 조회합니다.")
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionResponse> getPrescription(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId) {

        PrescriptionResponse response = prescriptionService.getPrescriptionDetails(userId, prescriptionId);
        return ResponseEntity.ok(response);
    }
}

