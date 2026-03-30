package com.carelink.symptom.repository;

import com.carelink.symptom.entity.SymptomEntity;
import com.carelink.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SymptomRepository extends JpaRepository<SymptomEntity, Long> {
    Optional<SymptomEntity> findBySymptomId(Long symptomId);
    List<SymptomEntity> findByUser(UserEntity user);
    List<SymptomEntity> findByUser_UserId(Long userId);
}
