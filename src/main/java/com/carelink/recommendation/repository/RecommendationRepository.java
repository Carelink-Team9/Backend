package com.carelink.recommendation.repository;

import com.carelink.recommendation.entity.RecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
}