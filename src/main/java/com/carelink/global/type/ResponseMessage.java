package com.carelink.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 성공 응답 메시지 enum. ApiResponse.ok() 에 사용, 기능 추가 시 여기에 추가
@Getter
@AllArgsConstructor
public enum ResponseMessage {

    REGISTER_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    POST_DELETE_SUCCESS("게시글이 삭제되었습니다."),
    COMMENT_DELETE_SUCCESS("댓글이 삭제되었습니다.");


    private final String message;
}
