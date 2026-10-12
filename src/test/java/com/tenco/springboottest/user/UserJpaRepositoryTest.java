package com.tenco.springboottest.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userJpaRepository.saveAndFlush(User.builder()
                .username("tester")
                .password("1234")
                .email("tester@example.com")
                .build());
        userId = user.getId();
        entityManager.clear();
    }

    @Test
    @DisplayName("저장한 회원의 아이디와 비밀번호가 일치하면 로그인 조회에 성공한다")
    void loginWithMatchingCredentials() {
        assertThat(userJpaRepository.loginQuery("tester", "1234"))
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(userId);
                    assertThat(user.getEmail()).isEqualTo("tester@example.com");
                });
    }

    @Test
    @DisplayName("아이디 또는 비밀번호가 틀리면 로그인 조회 결과가 없다")
    void loginWithInvalidCredentials() {
        assertThat(userJpaRepository.loginQuery("tester", "wrong")).isEmpty();
        assertThat(userJpaRepository.loginQuery("unknown", "1234")).isEmpty();
    }

    @Test
    @DisplayName("이미 등록된 사용자명을 조회한다")
    void findDuplicateUsername() {
        assertThat(userJpaRepository.checkDuplicationUsername("tester"))
                .hasValueSatisfying(user -> assertThat(user.getId()).isEqualTo(userId));
    }

    @Test
    @DisplayName("등록되지 않은 사용자명은 조회 결과가 없다")
    void findUnusedUsername() {
        assertThat(userJpaRepository.checkDuplicationUsername("new-user")).isEmpty();
    }

    @Test
    @DisplayName("이미 등록된 이메일을 조회한다")
    void findDuplicateEmail() {
        assertThat(userJpaRepository.checkDuplicationEmail("tester@example.com"))
                .hasValueSatisfying(user -> assertThat(user.getId()).isEqualTo(userId));
    }

    @Test
    @DisplayName("등록되지 않은 이메일은 조회 결과가 없다")
    void findUnusedEmail() {
        assertThat(userJpaRepository.checkDuplicationEmail("new@example.com")).isEmpty();
    }
}
