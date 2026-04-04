package com.carelink.comment.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentCreateRequest {

    @NotBlank
    private String content;

    @NotBlank
    private String language;
}