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
        String symptomInput;

        if (symptomsObj instanceof List) {
            // 1. 버튼식 입력일 경우: ["두통", "발열"] -> "두통, 발열"
            symptomInput = String.join(", ", (List<String>) symptomsObj);
        } else if (symptomsObj != null) {
            // 2. 직접 문장 입력일 경우: "배가 아픈거 같아" -> 그대로 사용
            symptomInput = symptomsObj.toString();
        } else {
            symptomInput = "";
        }

        // 이제 서비스는 항상 깨끗한 String을 받게 됩니다.
        DepartmentRecommendResponse response = recommendationService.getAndSaveRecommendation(userId, symptomInput);

        return ResponseEntity.ok(response);
    }
}