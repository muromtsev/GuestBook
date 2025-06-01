package com.example.guestbook.Controller;

import com.example.guestbook.Entity.GuestBookEntry;
import com.example.guestbook.Service.GuestBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class GuestBookController {

    private final GuestBookService guestBookService;

    @GetMapping
    public String index(Model model) {
        List<GuestBookEntry> entries = guestBookService.getGuestBookEntries();
        model.addAttribute("entry", new GuestBookEntry());
        model.addAttribute("entries", entries);
        return "index";
    }

    @PostMapping
    public String addEntry(@Valid @ModelAttribute("entry") GuestBookEntry entry,
                           BindingResult bindingResult,
                           Model model)
    {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "index";
        }

        entry.setCreatedAt(LocalDateTime.now());
        guestBookService.saveGuestBookEntry(entry);
        return "redirect:/";
    }



}
