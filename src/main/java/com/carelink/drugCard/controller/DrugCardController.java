package com.carelink.drugCard.controller;

import com.carelink.drugCard.dto.DrugCardDetailResponse;
import com.carelink.drugCard.dto.DrugCardListItemResponse;
import com.carelink.drugCard.service.DrugCardService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prescriptions/{prescriptionId}/drug-cards")
@Tag(name = "Drug Card", description = "처방전 약 카드 API")
@SecurityRequirement(name = "sessionCookieAuth")
public class DrugCardController {

    private final DrugCardService drugCardService;

    @Operation(summary = "약 카드 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DrugCardListItemResponse>>> getDrugCards(
            @CurrentUserId Long userId,
            @PathVariable Long prescriptionId
    ) {
        List<DrugCardListItemResponse> response = drugCardService.getDrugCards(userId, prescriptionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "약 카드 상세 조회")
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
