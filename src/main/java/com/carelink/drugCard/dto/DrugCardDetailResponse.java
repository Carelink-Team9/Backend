package com.carelink.drugCard.dto;

import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrugCardDetailResponse {

    private Long prescriptionDrugId; // String -> Long으로 수정
    private Long drugId;
    private String itemSeq;
    private String drugName;
    private String dosage;
    private String frequency;
    private String duration;
    private String efficacy;
    private String caution;
    private String useMethod;
    private String intrcQesitm;
    private String seQesitm;
    private String translatedContent;

    public static DrugCardDetailResponse from(PrescriptionDrugEntity prescriptionDrug) {
        return DrugCardDetailResponse.builder()
                .prescriptionDrugId(prescriptionDrug.getPrescriptionDrugId()) // 이제 타입이 호환됩니다.
                .drugId(prescriptionDrug.getDrug().getDrugId())
                .itemSeq(prescriptionDrug.getDrug().getItemSeq())
                .drugName(prescriptionDrug.getDrug().getName())
                .dosage(prescriptionDrug.getDosage())
                .frequency(prescriptionDrug.getFrequency())
                .duration(prescriptionDrug.getDuration())
                .efficacy(prescriptionDrug.getDrug().getEfficacy())
                .caution(prescriptionDrug.getDrug().getCaution())
                .useMethod(prescriptionDrug.getDrug().getUseMethod())
                .intrcQesitm(prescriptionDrug.getDrug().getIntrcQesitm())
                .seQesitm(prescriptionDrug.getDrug().getSeQesitm())
                .translatedContent(prescriptionDrug.getTranslatedContent())
                .build();
    }
}