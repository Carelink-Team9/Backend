package com.carelink.hospital.repository;

import com.carelink.hospital.entity.HospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<HospitalEntity, Long> {
    Optional<HospitalEntity> findByHospitalId(Long hospitalId);
    List<HospitalEntity> findByNameContaining(String name);
}
