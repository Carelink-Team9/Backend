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
        var drug = prescriptionDrug.getDrug();
        return DrugCardDetailResponse.builder()
                .prescriptionDrugId(prescriptionDrug.getPrescriptionDrugId())
                .drugId(drug != null ? drug.getDrugId() : null)
                .itemSeq(drug != null ? drug.getItemSeq() : null)
                .drugName(drug != null ? drug.getName() : prescriptionDrug.getOriginalName())
                .dosage(prescriptionDrug.getDosage())
                .frequency(prescriptionDrug.getFrequency())
                .duration(prescriptionDrug.getDuration())
                .efficacy(drug != null ? drug.getEfficacy() : null)
                .caution(drug != null ? drug.getCaution() : null)
                .useMethod(drug != null ? drug.getUseMethod() : null)
                .intrcQesitm(drug != null ? drug.getIntrcQesitm() : null)
                .seQesitm(drug != null ? drug.getSeQesitm() : null)
                .translatedContent(prescriptionDrug.getTranslatedContent())
                .build();
    }

    // 번역 결과로 필드를 업데이트하는 메서드 (확장)
    public void updateTranslations(String efficacy, String caution, String useMethod, String seQesitm, String intrcQesitm) {
        this.efficacy = efficacy;
        this.caution = caution;
        this.useMethod = useMethod;
        this.seQesitm = seQesitm;
        this.intrcQesitm = intrcQesitm;
    }
}

