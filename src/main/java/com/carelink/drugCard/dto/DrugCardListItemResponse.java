package com.carelink.drugCard.dto;

import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrugCardListItemResponse {

    private Long prescriptionDrugId; // String -> Long으로 수정!
    private Long drugId;
    private String drugName;
    private String dosage;
    private String frequency;
    private String duration;

    public static DrugCardListItemResponse from(PrescriptionDrugEntity prescriptionDrug) {
        return DrugCardListItemResponse.builder()
                .prescriptionDrugId(prescriptionDrug.getPrescriptionDrugId()) // 이제 타입이 일치합니다!
                .drugId(prescriptionDrug.getDrug().getDrugId())
                .drugName(prescriptionDrug.getDrug().getName())
                .dosage(prescriptionDrug.getDosage())
                .frequency(prescriptionDrug.getFrequency())
                .duration(prescriptionDrug.getDuration())
                .build();
    }
}