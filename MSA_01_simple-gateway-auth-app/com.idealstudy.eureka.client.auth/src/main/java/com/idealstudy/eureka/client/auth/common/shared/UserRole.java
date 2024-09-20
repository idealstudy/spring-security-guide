package com.idealstudy.eureka.client.auth.common.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {

    MASTER("ROLE_MASTER"), // 마스터
    COMPANY("ROLE_COMPANY"); // 업체

    private final String authority;

    // Secured 처리시 하드코딩을 줄이기 위한 클래스
    public static class Authority {

        public static final String MASTER = "ROLE_MASTER";
        public static final String COMPANY = "ROLE_COMPANY";
    }

}
