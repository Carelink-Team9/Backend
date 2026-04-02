package com.carelink.communityPost.entity;

import com.carelink.communityPost.entity.*;
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
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "translated_content", columnDefinition = "json")
    private String translatedContent;

    @Column(nullable = false)
    private String language;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "tag")
    private String tag;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private CommunityPostCategory category;

}
