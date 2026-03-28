package com.carelink.prescriptionDrug.repository;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescriptiondrug.entity.PrescriptionDrugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDrugRepository extends JpaRepository<PrescriptionDrugEntity, String> {
    Optional<PrescriptionDrugEntity> findByPrescriptionDrugId(String prescriptionDrugId);
    List<PrescriptionDrugEntity> findByPrescription(PrescriptionEntity prescription);
    List<PrescriptionDrugEntity> findByPrescription_PrescriptionId(Long prescriptionId);
    List<PrescriptionDrugEntity> findByDrug(DrugEntity drug);
    List<PrescriptionDrugEntity> findByDrug_DrugId(Long drugId);
}
