package com.carelink.drugHistory.repository;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.drugHistory.entity.DrugHistoryEntity;
import com.carelink.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrugHistoryRepository extends JpaRepository<DrugHistoryEntity, Long> {
    Optional<DrugHistoryEntity> findByDrugHistoryId(Long drugHistoryId);
    List<DrugHistoryEntity> findByUser(UserEntity user);
    List<DrugHistoryEntity> findByUser_UserId(Long userId);
    List<DrugHistoryEntity> findByDrug(DrugEntity drug);
    List<DrugHistoryEntity> findByDrug_DrugId(Long drugId);
}
