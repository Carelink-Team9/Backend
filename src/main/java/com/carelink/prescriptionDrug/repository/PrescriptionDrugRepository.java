package com.carelink.prescriptionDrug.repository;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDrugRepository extends JpaRepository<PrescriptionDrugEntity, String> {
    Optional<PrescriptionDrugEntity> findByPrescriptionDrugId(String prescriptionDrugId);
    List<PrescriptionDrugEntity> findByPrescription(PrescriptionEntity prescription);
    List<PrescriptionDrugEntity> findByPrescription_PrescriptionId(Long prescriptionId);
    List<PrescriptionDrugEntity> findByDrug(DrugEntity drug);
    List<PrescriptionDrugEntity> findByDrug_DrugId(Long drugId);

    @EntityGraph(attributePaths = "drug")
    @Query("select pd from PrescriptionDrugEntity pd where pd.prescription.prescriptionId = :prescriptionId")
    List<PrescriptionDrugEntity> findAllByPrescriptionIdWithDrug(@Param("prescriptionId") Long prescriptionId);

    @EntityGraph(attributePaths = {"drug", "prescription", "prescription.user"})
    @Query("select pd from PrescriptionDrugEntity pd where pd.prescriptionDrugId = :prescriptionDrugId")
    Optional<PrescriptionDrugEntity> findByIdWithDrugAndPrescription(@Param("prescriptionDrugId") String prescriptionDrugId);
}
