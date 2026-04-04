package com.carelink.communityPost.entity.dto;

import com.carelink.communityPost.entity.CommunityPostEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityPostResponse {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String language;
    private String tag;
    private String category;
    private LocalDateTime createdAt;

    public static CommunityPostResponse from(CommunityPostEntity entity, String Title, String content) {
        return CommunityPostResponse.builder()
                .postId(entity.getCommunityPostId())
                .userId(entity.getUser().getUserId())
                .title(Title)
                .content(content)
                .language(entity.getLanguage())
                .tag(entity.getTag())
                .category(entity.getCategory().name())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
