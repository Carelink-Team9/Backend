package com.carelink.recommendation.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.carelink.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/department")
    public ResponseEntity<DepartmentRecommendResponse> recommend(
            @CurrentUserId Long userId,
            @RequestBody List<String> symptoms) {

        DepartmentRecommendResponse response = recommendationService.getAndSaveRecommendation(userId, symptoms);
        return ResponseEntity.ok(response);
    }
}