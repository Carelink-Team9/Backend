package com.carelink.recommendation.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.recommendation.dto.DepartmentRecommendRequest;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.carelink.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "진료과 추천 API")
@SecurityRequirement(name = "sessionCookieAuth")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "진료과 추천", description = "증상을 바탕으로 적절한 진료과를 추천합니다.")
    @PostMapping("/department")
    public ResponseEntity<DepartmentRecommendResponse> recommend(
            @CurrentUserId Long userId,
            @RequestBody DepartmentRecommendRequest request) {

        Object symptomsObj = request.getSymptoms();
        String customDescription = request.getCustomDescription();

        String symptomInput;

        if (symptomsObj instanceof List<?> symptoms) {
            // 버튼 선택 증상: 항상 한국어 키워드
            symptomInput = String.join(", ", symptoms.stream().map(String::valueOf).toList());
        } else if (symptomsObj != null) {
            symptomInput = symptomsObj.toString();
        } else {
            symptomInput = "";
        }

        // 자유 입력이 있으면 별도로 추가 (언어 무관)
        if (customDescription != null && !customDescription.isBlank()) {
            symptomInput = symptomInput.isBlank()
                    ? customDescription
                    : symptomInput + " | Additional description: " + customDescription;
        }

        DepartmentRecommendResponse response = recommendationService.getAndSaveRecommendation(userId, symptomInput);
        return ResponseEntity.ok(response);
    }
}
