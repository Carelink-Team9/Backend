package com.carelink.communityPost.entity;

import com.carelink.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "community_post")
@EntityListeners(AuditingEntityListener.class)
public class CommunityPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_id")
    private Long communityPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "translated_content", columnDefinition = "json")
    private String translatedContent;

    @Column(name = "translated_title", columnDefinition = "json")
    private String translatedTitle;

    private String language;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "tag", columnDefinition = "json")
    private String tag;

    @Enumerated(EnumType.STRING)
    private com.carelink.communityPost.entity.CommunityPostCategory category;


}
