package com.tenco.springboottest.board;

import com.tenco.springboottest._core.error.Exception403;
import com.tenco.springboottest._core.error.Exception404;
import com.tenco.springboottest.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BoardService {

    private final BoardJpaRepository boardJpaRepository;

    public List<Board> findAll() {
        return boardJpaRepository.findAllList();
    }

    public Board detail(Long id) {
        return boardJpaRepository.boardDetail(id)
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));
    }

    @Transactional
    public Board save(BoardRequest.SaveDto saveDto, User sessionUser) {
        Board board = saveDto.toEntity(sessionUser);
        Board savedBoard = boardJpaRepository.save(board);
        return savedBoard;
    }

    @Transactional
    public void deleteById(Long id, User sessionUser) {
        Board board = detail(id);
        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }
        boardJpaRepository.deleteById(id);
    }

    @Transactional
    public Board update(Long id, BoardRequest.UpdateDto updateDto, User sessionUser) {
        Board board = detail(id);
        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("권한없음");
        }
        board.update(updateDto);
        return board;
    }
}
