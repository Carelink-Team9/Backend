package com.carelink.communityPost.entity.dto;

import com.carelink.communityPost.entity.CommunityPostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommunityPostCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String tag;

    @NotNull
    private CommunityPostCategory category;
}
