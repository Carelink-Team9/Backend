package com.carelink.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRecommendResponse {
    private String mainDepartment;           // 한국어 원문 (예: 신경과)
    private String translatedMainDepartment; // 번역본 (예: 神经科)
    private int mainConfidence;

    private String reason;                   // 한국어 추천 이유
    private String translatedReason;         // 번역된 추천 이유

    private List<AlternativeDept> alternatives;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlternativeDept {
        private String departmentName;           // 한국어 원문
        private String translatedDepartmentName; // 번역본
        private int confidence;
    }
}