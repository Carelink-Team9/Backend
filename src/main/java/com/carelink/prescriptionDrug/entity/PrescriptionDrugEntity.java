package com.carelink.prescriptiondrug.entity;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.prescription.entity.PrescriptionEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "prescription_drug")
public class PrescriptionDrugEntity {

    @Id
    @Column(name = "prescription_drug_id", length = 100)
    private String prescriptionDrugId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private PrescriptionEntity prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private DrugEntity drug;

    private String dosage;

    private String frequency;

    private String duration;

    @Column(name = "translated_content", columnDefinition = "TEXT")
    private String translatedContent;
}
