package com.carelink.recommendation.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.carelink.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/department")
    public ResponseEntity<DepartmentRecommendResponse> recommend(
            @CurrentUserId Long userId,
            @RequestBody Map<String, Object> request) { // <--- String 대신 Object로 받아야 리스트/문장 둘 다 대응 가능

        Object symptomsObj = request.get("symptoms");
        String customDescription = request.get("customDescription") != null
                ? request.get("customDescription").toString()
                : null;

        String symptomInput;

        if (symptomsObj instanceof List) {
            // 버튼 선택 증상: 항상 한국어 키워드
            symptomInput = String.join(", ", (List<String>) symptomsObj);
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