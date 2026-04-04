package com.carelink.communityPost.service;

import com.carelink.comment.repository.CommentRepository;
import com.carelink.communityPost.entity.CommunityPostEntity;
import com.carelink.communityPost.entity.dto.CommunityPostCreateRequest;
import com.carelink.communityPost.entity.dto.CommunityPostResponse;
import com.carelink.communityPost.entity.dto.CommunityPostUpdateRequest;
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
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final TranslationService translationService;
    private final CommentRepository commentRepository;

    @Transactional
    public CommunityPostResponse create(Long userId, CommunityPostCreateRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        CommunityPostEntity post = CommunityPostEntity.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .language(request.getLanguage())
                .tag(request.getTag())
                .category(request.getCategory())
                .build();

        CommunityPostEntity savePost = communityPostRepository.save(post);
        return CommunityPostResponse.from(savePost, savePost.getContent(), 0);
    }

    @Transactional(readOnly = true)
    public CommunityPostResponse getById(Long postId, String targetLanguage) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        //번역 translate에 원문 내용, 원문 언어, 요청자 언어를 보냄
        String translatedContent = translationService.translate(
                post.getContent(),
                post.getLanguage(),
                targetLanguage
        );

        long commentCount = commentRepository.countByCommunityPost_CommunityPostId(postId);
        return CommunityPostResponse.from(post, translatedContent, commentCount);
    }

    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getByTag(String tag, String targetLanguage) {
        return communityPostRepository.findByTagContaining(tag).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        translationService.translate(post.getContent(), post.getLanguage(), targetLanguage),
                        commentRepository.countByCommunityPost_CommunityPostId(post.getCommunityPostId())
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getByLanguage(String language, String targetLanguage) {
        return communityPostRepository.findByLanguage(language).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        translationService.translate(post.getContent(), post.getLanguage(), targetLanguage),
                        commentRepository.countByCommunityPost_CommunityPostId(post.getCommunityPostId())
                ))
                .toList();
    }

    @Transactional
    public CommunityPostResponse update(Long userId, Long postId, CommunityPostUpdateRequest request) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        validateOwner(userId, post.getUser().getUserId());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLanguage(request.getLanguage());
        post.setTag(request.getTag());
        post.setCategory(request.getCategory());
        post.setTranslatedContent(null);

        long commentCount = commentRepository.countByCommunityPost_CommunityPostId(postId);
        return CommunityPostResponse.from(post, post.getContent(), commentCount);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        validateOwner(userId, post.getUser().getUserId());
        communityPostRepository.delete(post);
    }

    private void validateOwner(Long currentUserId, Long ownerId) {
        if (!currentUserId.equals(ownerId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }
    }
}
