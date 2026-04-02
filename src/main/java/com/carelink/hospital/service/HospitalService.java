package com.carelink.hospital.service;

import com.carelink.hospital.dto.HospitalNearbyResponse;
import com.carelink.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private static final double EARTH_RADIUS_KM = 111.0;

    private final HospitalRepository hospitalRepository;

    @Transactional(readOnly = true)
    public List<HospitalNearbyResponse> findNearby(double lat, double lng, double radiusKm, int limit) {
        // 바운딩 박스 계산 (Haversine 전 MySQL에서 1차 필터링용)
        double latDelta = radiusKm / EARTH_RADIUS_KM;
        double lngDelta = radiusKm / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(lat)));

        return hospitalRepository
                .findNearbyHospitals(lat, lng,
                        lat - latDelta, lat + latDelta,
                        lng - lngDelta, lng + lngDelta,
                        radiusKm, limit)
                .stream()
                .map(HospitalNearbyResponse::fromRow)
                .toList();
    }
}
