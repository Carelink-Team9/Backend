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

    /**
     * 진료과 기반 병원 검색:
     *  1) 병원명에 진료과 키워드 포함 (예: "강남내과의원")
     *  2) 종별이 진료과 키워드 포함 (예: 치과 → "치과의원", "치과병원")
     *  3) 상급종합/종합병원은 전 진료과 보유이므로 항상 포함
     */
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
              AND (
                    name       LIKE %:dept%
                 OR department LIKE %:dept%
                 OR department IN ('상급종합', '종합병원')
              )
            HAVING distance <= :radiusKm
            ORDER BY distance
            LIMIT :lim
            """, nativeQuery = true)
    List<Object[]> findNearbyHospitalsByDepartment(
            @Param("lat")      double lat,
            @Param("lng")      double lng,
            @Param("minLat")   double minLat,
            @Param("maxLat")   double maxLat,
            @Param("minLng")   double minLng,
            @Param("maxLng")   double maxLng,
            @Param("radiusKm") double radiusKm,
            @Param("lim")      int lim,
            @Param("dept")     String dept
    );
}
