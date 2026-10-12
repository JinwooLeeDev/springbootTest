package com.tenco.springboottest.board;

import com.tenco.springboottest._core.error.Exception403;
import com.tenco.springboottest._core.util.Define;
import com.tenco.springboottest.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller // IoC
public class BoardController {

    private final BoardService boardService;

    @GetMapping({"/", "/board/list"})
    public String list(Model model) {
        List<Board> boardList = boardService.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    @GetMapping("/board/{id}")
    public String listDetail(@PathVariable(name = "id") Long id, Model model){
        Board board = boardService.detail(id);
        model.addAttribute("board", board);
        return "board/detail";
    }

    @GetMapping("/board/save")
    public String saveTab() {
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDto saveDto, HttpSession session) {
        saveDto.validate();
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        boardService.save(saveDto, sessionUser);
        return "redirect:/";
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable Long id,
                         HttpSession session) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        boardService.deleteById(id, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/board/{id}/update")
    public String updateTab(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.detail(id);
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("권한 없음");
        }
        model.addAttribute("board", board);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String updatePost(@PathVariable Long id,
                             BoardRequest.UpdateDto updateDto,
                             HttpSession session) {
        updateDto.validate();
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        boardService.update(id, updateDto, sessionUser);
        return "redirect:/board/" + id;
    }
}
