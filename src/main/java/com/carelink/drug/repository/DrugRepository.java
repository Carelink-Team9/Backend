package com.carelink.drug.repository;

import com.carelink.drug.entity.DrugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrugRepository extends JpaRepository<DrugEntity, Long> {
    Optional<DrugEntity> findByDrugId(Long drugId);
    List<DrugEntity> findByNameContaining(String name);

    // 추가: itemSeq로 약을 찾는 메서드
    Optional<DrugEntity> findByItemSeq(String itemSeq);
}
