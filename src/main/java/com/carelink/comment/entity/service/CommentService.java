package com.carelink.comment.entity.service;

import com.carelink.comment.entity.CommentEntity;
import com.carelink.comment.entity.dto.CommentCreateRequest;
import com.carelink.comment.entity.dto.CommentResponse;
import com.carelink.comment.entity.dto.CommentUpdateRequest;
import com.carelink.comment.repository.CommentRepository;
import com.carelink.communityPost.entity.CommunityPostEntity;
import com.carelink.communityPost.repository.CommunityPostRepository;
import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.translation.service.TranslationService;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final TranslationService translationService;


    @Transactional
    public CommentResponse create(Long userId, Long postId, CommentCreateRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        CommentEntity comment = CommentEntity.builder()
                .user(user)
                .communityPost(post)
                .content(request.getContent())
                .language(request.getLanguage())
                .build();

        CommentEntity savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment, savedComment.getContent());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getByPostId(Long postId, String targetLanguage) {
        communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        return commentRepository.findByCommunityPost_CommunityPostId(postId).stream()
                .map(comment -> CommentResponse.from(
                        comment,
                        translationService.translate(
                                comment.getContent(),
                                comment.getLanguage(),
                                targetLanguage
                        )
                ))
                .toList();
    }


    @Transactional
    public CommentResponse update(Long userId, Long commentId, CommentUpdateRequest request) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        validateOwner(userId, comment.getUser().getUserId());

        comment.setContent(request.getContent());
        comment.setLanguage(request.getLanguage());
        comment.setTranslatedContent(null);

        return CommentResponse.from(comment, comment.getContent());
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        validateOwner(userId, comment.getUser().getUserId());
        commentRepository.delete(comment);
    }

    private void validateOwner(Long currentUserId, Long ownerId) {
        if (!currentUserId.equals(ownerId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }
    }
}