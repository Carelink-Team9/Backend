package com.carelink.prescription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrescriptionResponse {
    private Long prescriptionId;
    private LocalDateTime createdAt;
    private List<DrugDetailDto> drugs;

    @Getter
    @AllArgsConstructor
    public static class DrugDetailDto {
        private String drugName;      // DB 검색된 약 이름
        private String dosage;        // 용량
        private String frequency;     // 횟수
        private String duration;      // 기간
        private String translatedContent; // 유저 언어별 복용 가이드
    }
}