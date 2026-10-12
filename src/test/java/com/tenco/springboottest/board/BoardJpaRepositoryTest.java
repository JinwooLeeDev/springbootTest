package com.tenco.springboottest.board;

import com.tenco.springboottest.user.User;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class BoardJpaRepositoryTest {

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("게시글 목록을 ID 내림차순으로 작성자와 함께 조회한다")
    void findAllWithAuthorsInDescendingIdOrder() {
        User firstAuthor = saveUser("first");
        User secondAuthor = saveUser("second");
        Board first = saveBoard("첫 번째 글", firstAuthor);
        Board second = saveBoard("두 번째 글", secondAuthor);
        Board third = saveBoard("세 번째 글", firstAuthor);
        flushAndClear();

        List<Board> boards = boardJpaRepository.findAllList();

        assertThat(boards).extracting(Board::getId)
                .containsExactly(third.getId(), second.getId(), first.getId());
        // 작성자를 먼저 접근하면 지연 로딩될 수 있으므로 초기화 여부부터 검사한다.
        assertThat(boards).allSatisfy(board ->
                assertThat(Hibernate.isInitialized(board.getUser())).isTrue());
        entityManager.clear();
        assertThat(boards).extracting(board -> board.getUser().getUsername())
                .containsExactly("first", "second", "first");
    }

    @Test
    @DisplayName("게시글이 없으면 빈 목록을 반환한다")
    void findAllWhenEmpty() {
        assertThat(boardJpaRepository.findAllList()).isEmpty();
    }

    @Test
    @DisplayName("지정한 게시글의 내용과 작성자를 함께 조회한다")
    void findDetailWithAuthor() {
        User author = saveUser("writer");
        saveBoard("다른 글", author);
        Board target = saveBoard("조회할 글", author);
        flushAndClear();

        assertThat(boardJpaRepository.boardDetail(target.getId()))
                .hasValueSatisfying(board -> {
                    assertThat(board.getId()).isEqualTo(target.getId());
                    assertThat(board.getTitle()).isEqualTo("조회할 글");
                    assertThat(board.getContent()).isEqualTo("본문 내용");
                    assertThat(Hibernate.isInitialized(board.getUser())).isTrue();
                    entityManager.clear();
                    assertThat(board.getUser().getUsername()).isEqualTo("writer");
                });
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID는 조회 결과가 없다")
    void findMissingDetail() {
        saveBoard("존재하는 글", saveUser("writer"));
        flushAndClear();

        assertThat(boardJpaRepository.boardDetail(-1L)).isEmpty();
    }

    private User saveUser(String username) {
        User user = User.builder()
                .username(username)
                .password("1234")
                .email(username + "@example.com")
                .build();
        entityManager.persist(user);
        return user;
    }

    private Board saveBoard(String title, User author) {
        return boardJpaRepository.save(Board.builder()
                .title(title)
                .content("본문 내용")
                .user(author)
                .build());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
