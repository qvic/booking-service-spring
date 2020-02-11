package com.epam.bookingservice.controller;

import com.epam.bookingservice.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ModelAndView showUsers(Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("users");
        modelAndView.addObject("usersPage", userService.findAll(pageable));
        return modelAndView;
    }
}
