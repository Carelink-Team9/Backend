package com.carelink.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class PrescriptionSummaryResponse {
    private Long prescriptionId;
    private int totalDrugCount;
    private LocalDateTime prescribedAt;
}