package com.demo.springsecurity.common.exception

import com.fasterxml.jackson.annotation.JsonGetter
import org.springframework.http.HttpStatus

enum class ExceptionCode(val httpStatus: HttpStatus, val code: String, val message: String? = null) {
    /**
     * 4xx Client Error
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "중복된 리소스 형식입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 설정되어 있지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),

    /**
     * 5xx Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),

    /**
     * Admin Login Error
     */
    LOGIN_ID_FAIL(HttpStatus.BAD_REQUEST, "ADM-LOGIN-1", "감별마켓 ADMIN 아이디를 올바르게 입력해 주세요."),
    LOGIN_PW_FAIL(HttpStatus.BAD_REQUEST, "ADM-LOGIN-2", "감별마켓 ADMIN 비밀번호를 올바르게 입력해 주세요."),
    INVALID_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "ADM-LOGIN-3", "8~16자의 숫자와 영문자, 또는 특수문자를 사용하여 입력해주세요."),
    DUPLICATE_NEW_PASSWORD(HttpStatus.CONFLICT, "ADM-LOGIN-4", "기존 패스워드와 동일합니다."),

    /**
     * Admin User/Auth Manage Error
     */
    DUPLICATE_GROUP_NAME(HttpStatus.CONFLICT, "ADM-MNG-1", "중복된 권한 그룹 명 입니다."),
    DUPLICATE_ADMIN_ID(HttpStatus.NOT_FOUND, "ADM-MNG-2", "현재 사용중인 계정입니다."),

    /**
     * Admin Store Manage Error
     */
    DUPLICATE_MALL_ID(HttpStatus.CONFLICT, "ADM-SHOP-1", "이미 등록된 mall_id 입니다."),
    DUPLICATE_CORP_NO(HttpStatus.CONFLICT, "ADM-SHOP-2", "중복된 사업자 번호입니다.")
}