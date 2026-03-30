package com.carelink.communityPost.repository;

import com.carelink.communityPost.entity.CommunityPostCategory;
import com.carelink.communityPost.entity.CommunityPostEntity;
import com.carelink.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {
    Optional<CommunityPostEntity> findByCommunityPostId(Long communityPostId);
    List<CommunityPostEntity> findByUser(UserEntity user);
    List<CommunityPostEntity> findByUser_UserId(Long userId);
    List<CommunityPostEntity> findByCategory(CommunityPostCategory category);
}
