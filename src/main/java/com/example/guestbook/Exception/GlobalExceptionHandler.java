package com.example.guestbook.Exception;

import com.example.guestbook.Entity.GuestBookEntry;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e, Model model) {
        model.addAttribute("error", "Произошла ошибка: " + e.getMessage());
        model.addAttribute("entry", new GuestBookEntry());
        model.addAttribute("entries", List.of());
        return "index";
    }


}
