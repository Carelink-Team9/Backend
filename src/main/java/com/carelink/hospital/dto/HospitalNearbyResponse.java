package com.carelink.hospital.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    /**
     * native query Object[] 행 순서:
     * 0=hospital_id, 1=name, 2=address, 3=department, 4=phone,
     * 5=latitude,    6=longitude, 7=sido_nm, 8=sggu_nm, 9=homepage, 10=distance
     */
    public static HospitalNearbyResponse fromRow(Object[] row) {
        double rawDist = row[10] != null ? ((Number) row[10]).doubleValue() : 0.0;
        double dist = BigDecimal.valueOf(rawDist)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return new HospitalNearbyResponse(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                row[5] != null ? new BigDecimal(row[5].toString()) : null,
                row[6] != null ? new BigDecimal(row[6].toString()) : null,
                (String) row[7],
                (String) row[8],
                (String) row[9],   // homepage (null이면 null 그대로)
                dist
        );
    }
}
