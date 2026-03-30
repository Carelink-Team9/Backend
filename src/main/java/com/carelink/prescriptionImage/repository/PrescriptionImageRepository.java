package com.carelink.prescriptionImage.repository;

import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescriptionImage.entity.PrescriptionImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionImageRepository extends JpaRepository<PrescriptionImageEntity, Long> {
    Optional<PrescriptionImageEntity> findByPrescriptionImageId(Long prescriptionImageId);
    List<PrescriptionImageEntity> findByPrescription(PrescriptionEntity prescription);
    List<PrescriptionImageEntity> findByPrescription_PrescriptionId(Long prescriptionId);
}
