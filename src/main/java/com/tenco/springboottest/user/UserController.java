package com.tenco.springboottest.user;

import com.tenco.springboottest._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    @GetMapping("/logout")
    public String logoutForm(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(UserRequest.LoginDto loginDto, HttpSession session) {
        loginDto.validate();
        User user = userService.login(loginDto);
        user.setPassword(null);
        session.setAttribute(Define.SESSION_USER, user);
        return "redirect:/";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "user/join-form";
    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDto joinDto) {
        joinDto.validate();
        userService.join(joinDto);
        return "redirect:/login";
    }

    @PostMapping("/user/update")
    public String updateUser(HttpSession session, UserRequest.UpdateDto updateDto) {
        updateDto.validate();
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        User updateUser = userService.updateUserInfo(sessionUser.getId(), updateDto);
        updateUser.setPassword(null);
        session.setAttribute(Define.SESSION_USER, updateUser);
        return "redirect:/";
    }

    @GetMapping("/user/update")
    public String updateUserTab(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        User user = userService.userInfo(sessionUser.getId());
        model.addAttribute("user", userService.userInfo(user.getId()));
        return "user/update-form";
    }
}
