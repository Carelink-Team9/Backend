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

    @Transactional(readOnly = true)
    public List<HospitalNearbyResponse> findNearbyByDepartment(double lat, double lng, double radiusKm, int limit, String department) {
        double latDelta = radiusKm / EARTH_RADIUS_KM;
        double lngDelta = radiusKm / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(lat)));

        return hospitalRepository
                .findNearbyHospitalsByDepartment(lat, lng,
                        lat - latDelta, lat + latDelta,
                        lng - lngDelta, lng + lngDelta,
                        radiusKm, limit, department)
                .stream()
                .map(HospitalNearbyResponse::fromRow)
                .toList();
    }
}
