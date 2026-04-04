package com.carelink.comment.repository;

import com.carelink.comment.entity.CommentEntity;
import com.carelink.communityPost.entity.CommunityPostEntity;
import com.carelink.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<CommentEntity> findByCommentId(Long commentId);
    List<CommentEntity> findByUser(UserEntity user);
    List<CommentEntity> findByUser_UserId(Long userId);
    List<CommentEntity> findByCommunityPost(CommunityPostEntity communityPost);
    List<CommentEntity> findByCommunityPost_CommunityPostId(Long communityPostId);
    long countByCommunityPost_CommunityPostId(Long communityPostId);
}
