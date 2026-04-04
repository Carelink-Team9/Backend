package com.carelink.drugCard.dto;

import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrugCardListItemResponse {

    private Long prescriptionDrugId;
    private Long drugId;
    private String drugName;
    private String dosage;
    private String frequency;
    private String duration;
    // --- 팀원 요청 필드 추가 ---
    private String caution;           // 주의사항
    private String seQesitm;          // 부작용
    private String intrcQesitm;       // 상호작용
    private String translatedContent; // GPT가 생성한 번역 요약

    public static DrugCardListItemResponse from(PrescriptionDrugEntity prescriptionDrug) {
        return DrugCardListItemResponse.builder()
                .prescriptionDrugId(prescriptionDrug.getPrescriptionDrugId())
                .drugId(prescriptionDrug.getDrug().getDrugId())
                .drugName(prescriptionDrug.getDrug().getName())
                .dosage(prescriptionDrug.getDosage())
                .frequency(prescriptionDrug.getFrequency())
                .duration(prescriptionDrug.getDuration())
                // --- 매핑 추가 ---
                .caution(prescriptionDrug.getDrug().getCaution())
                .seQesitm(prescriptionDrug.getDrug().getSeQesitm())
                .intrcQesitm(prescriptionDrug.getDrug().getIntrcQesitm())
                .translatedContent(prescriptionDrug.getTranslatedContent())
                .build();
    }
}