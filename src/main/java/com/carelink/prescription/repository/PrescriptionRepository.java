package com.carelink.prescription.repository;

import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity, Long> {
    Optional<PrescriptionEntity> findByPrescriptionId(Long prescriptionId);
    List<PrescriptionEntity> findByUser(UserEntity user);
    List<PrescriptionEntity> findByUser_UserId(Long userId);
}
