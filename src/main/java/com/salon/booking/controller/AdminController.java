package com.salon.booking.controller;

import com.salon.booking.domain.User;
import com.salon.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int DEFAULT_USERS_PER_PAGE = 5;

    private final UserService userService;

    @GetMapping
    public String homePage() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String showUsers(@PageableDefault(size = DEFAULT_USERS_PER_PAGE) Pageable pageable, Model model) {
        Page<User> users = userService.findAll(pageable);
        model.addAttribute("usersPage", users);
        return "admin/users";
    }
}
