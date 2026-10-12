package com.tenco.springboottest._core.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception400.class)
    public String ex400(Exception400 e, HttpServletRequest request, Model model) {
        model.addAttribute("msg", e.getMessage());
        return "err/400";
    }

    @ExceptionHandler(Exception401.class)
    public String ex401(Exception401 e, HttpServletRequest request, RedirectAttributes rttr) {
        rttr.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(Exception403.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String ex403(Exception403 e, HttpServletRequest request, Model model) {
        String msg = e.getMessage().replace("'", "\\'"); // 따옴표 깨짐 방지
        return """
                <script>
                    alert('%s');
                    history.back();
                </script>
                """.formatted(msg);
    }

    @ExceptionHandler(Exception404.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ex404(Exception404 e, HttpServletRequest request, Model model) {
        model.addAttribute("msg", e.getMessage());
        return "err/404";
    }

    @ExceptionHandler(Exception500.class)
    public String ex500(Exception500 e, HttpServletRequest request, Model model) {
        model.addAttribute("msg", "네트워크 일시적 장애");
        return "err/500";
    }

    @ExceptionHandler(Exception.class)
    public String ex(RuntimeException e, HttpServletRequest request, Model model) {
        model.addAttribute("msg", "시스템 오류 발생, 관리자에게 문의해주세요.");
        return "err/500";
    }
}
