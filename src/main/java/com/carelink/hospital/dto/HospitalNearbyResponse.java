package com.carelink.hospital.dto;

import com.carelink.hospital.repository.HospitalDistanceProjection;

import java.math.BigDecimal;

public record HospitalNearbyResponse(
        Long hospitalId,
        String name,
        String address,
        String department,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        String sidoNm,
        String sgguNm,
        String homepage,
        double distanceKm
) {
    public static HospitalNearbyResponse from(HospitalDistanceProjection p) {
        return new HospitalNearbyResponse(
                p.getHospitalId(),
                p.getName(),
                p.getAddress(),
                p.getDepartment(),
                p.getPhone(),
                p.getLatitude(),
                p.getLongitude(),
                p.getSidoNm(),
                p.getSgguNm(),
                p.getHomepage(),
                p.getDistance() != null
                        ? Math.round(p.getDistance() * 100.0) / 100.0
                        : 0.0
        );
    }
}
