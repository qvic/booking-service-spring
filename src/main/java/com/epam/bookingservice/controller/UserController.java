package com.epam.bookingservice.controller;

import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private static final int DEFAULT_USERS_PER_PAGE = 5;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String indexPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/users";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/users";
        }
        return "login";
    }

    @GetMapping("/sign-up")
    public String registrationPage() {
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String proceedRegistration(User user, HttpSession session) {
        session.invalidate();
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String showUsers(@PageableDefault(size = DEFAULT_USERS_PER_PAGE) Pageable pageable, Model model) {
        Page<User> users = userService.findAll(pageable);
        model.addAttribute("usersPage", users);
        return "users";
    }
}
