package com.carelink.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRecommendRequest {

    @Schema(
            description = "Symptoms as either a free-text string or an array of symptom strings",
            example = "[\"headache\", \"fever\"]"
    )
    private Object symptoms;

    @Schema(description = "Additional free-text description in any language", example = "배가 많이 아파요")
    private String customDescription;
}
