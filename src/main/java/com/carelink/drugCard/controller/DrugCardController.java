package com.carelink.drugCard.controller;

import com.carelink.drugCard.dto.DrugCardDetailResponse;
import com.carelink.drugCard.dto.DrugCardListItemResponse;
import com.carelink.drugCard.service.DrugCardService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prescriptions/{prescriptionId}/drug-cards")
public class DrugCardController {

    private final DrugCardService drugCardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DrugCardListItemResponse>>> getDrugCards(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId
    ) {
        List<DrugCardListItemResponse> response = drugCardService.getDrugCards(userId, prescriptionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
        //처방전 약리스트
    }

    @GetMapping("/{prescriptionDrugId}")
    public ResponseEntity<ApiResponse<DrugCardDetailResponse>> getDrugCard(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId,
            @PathVariable String prescriptionDrugId
    ) {
        DrugCardDetailResponse response = drugCardService.getDrugCard(userId, prescriptionId, prescriptionDrugId);
        return ResponseEntity.ok(ApiResponse.ok(response));
        //처방전 약 상세(클릭했을때)
    }
}
