package com.carelink.drug.repository;

import com.carelink.drug.entity.DrugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrugRepository extends JpaRepository<DrugEntity, Long> {
    Optional<DrugEntity> findByDrugId(Long drugId);
    List<DrugEntity> findByNameContaining(String name);
}
