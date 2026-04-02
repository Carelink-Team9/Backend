package com.carelink.drugCard.dto;

import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter // 번역 결과를 업데이트하기 위해 추가
public class DrugCardDetailResponse {

    private Long prescriptionDrugId;
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
                .prescriptionDrugId(prescriptionDrug.getPrescriptionDrugId())
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

    // 번역 결과로 필드를 업데이트하는 메서드
    public void updateTranslations(String efficacy, String caution, String useMethod) {
        this.efficacy = efficacy;
        this.caution = caution;
        this.useMethod = useMethod;
    }
}

