package com.tenco.springboottest.user;

import com.tenco.springboottest._core.error.Exception400;
import lombok.Data;

public class UserRequest {

    @Data
    public static class LoginDto {
        private String username;
        private String password;

        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new Exception400("사용자명은 필수 입니다.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new Exception400("비밀번호는 필수 입니다.");
            }
        }
    }

    @Data
    public static class JoinDto {
        private String username;
        private String password;
        private String email;

        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new Exception400("사용자명은 필수 입니다.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new Exception400("비밀번호는 필수 입니다.");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new Exception400("이메일은 필수 입니다.");
            }
        }
    }

    @Data
    public static class UpdateDto {
        private String password;

        public void validate() {
            if (password == null || password.trim().isEmpty()) {
                throw new Exception400("올바른 비밀번호 형식이 아닙니다.");
            }
            if (password.length() < 4) {
                throw new Exception400("비밀번호는 반드시 4자리 이상이어야 합니다.");
            }
        }
    }
}
