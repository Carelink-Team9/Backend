package com.carelink.drugCard.controller;

import com.carelink.drugCard.dto.DrugCardDetailResponse;
import com.carelink.drugCard.dto.DrugCardListItemResponse;
import com.carelink.drugCard.service.DrugCardService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    }

    @GetMapping("/{prescriptionDrugId}")
    public ResponseEntity<ApiResponse<DrugCardDetailResponse>> getDrugCard(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId,
            @PathVariable String prescriptionDrugId,
            @RequestParam(value = "translate", defaultValue = "false") boolean translate // 추가됨
    ) {
        DrugCardDetailResponse response = drugCardService.getDrugCard(userId, prescriptionId, prescriptionDrugId, translate);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}