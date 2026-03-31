package com.carelink.prescriptionImage.entity;

import com.carelink.prescription.entity.PrescriptionEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "prescription_image")
public class PrescriptionImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_image_id")
    private Long prescriptionImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private PrescriptionEntity prescription;

    @Column(name = "image_url")
    private String imageUrl;
}
