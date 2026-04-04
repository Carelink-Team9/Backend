package com.carelink.prescriptionDrug.entity;

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
public class
PrescriptionDrugEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_drug_id", length = 100)
    private Long prescriptionDrugId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private PrescriptionEntity prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private DrugEntity drug;

    @Column(name = "original_name")
    private String originalName;

    private String dosage;

    private String frequency;

    private String duration;

    @Column(name = "translated_content", columnDefinition = "TEXT")
    private String translatedContent;
}
