package com.carelink.hospital.repository;

import com.carelink.hospital.entity.HospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface HospitalRepository extends JpaRepository<HospitalEntity, Long> {
    Optional<HospitalEntity> findByHospitalId(Long hospitalId);
    List<HospitalEntity> findByNameContaining(String name);

    @Query(value = """
            SELECT hospital_id, name, address, department, phone,
                   latitude, longitude, sido_nm, sggu_nm, homepage,
                   (6371 * acos(
                       cos(radians(:lat)) * cos(radians(latitude))
                       * cos(radians(longitude) - radians(:lng))
                       + sin(radians(:lat)) * sin(radians(latitude))
                   )) AS distance
            FROM hospital
            WHERE latitude  BETWEEN :minLat AND :maxLat
              AND longitude BETWEEN :minLng AND :maxLng
            HAVING distance <= :radiusKm
            ORDER BY distance
            LIMIT :lim
            """, nativeQuery = true)
    List<Object[]> findNearbyHospitals(
            @Param("lat")      double lat,
            @Param("lng")      double lng,
            @Param("minLat")   double minLat,
            @Param("maxLat")   double maxLat,
            @Param("minLng")   double minLng,
            @Param("maxLng")   double maxLng,
            @Param("radiusKm") double radiusKm,
            @Param("lim")      int lim
    );
}
