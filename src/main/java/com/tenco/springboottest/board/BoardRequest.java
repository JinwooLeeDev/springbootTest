package com.tenco.springboottest.board;

import com.tenco.springboottest._core.error.Exception400;
import com.tenco.springboottest.user.User;
import lombok.Data;

public class BoardRequest {
    @Data
    public static class SaveDto {
        private String title;
        private String content;

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new Exception400("제목은 필수입니다");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new Exception400("내용은 필수입니다");
            }
        }

        public Board toEntity(User user) {
            return Board.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user)
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        private String title;
        private String content;

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new Exception400("제목은 필수입니다");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new Exception400("내용은 필수입니다");
            }
        }
    }
}
