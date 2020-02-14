package com.epam.bookingservice.controller;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.domain.UserLoginForm;
import com.epam.bookingservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final AuthService authService;
    private static final Map<Role, String> ROLE_TO_HOME_PAGE = initializeHomePages();

    @GetMapping("/")
    public String indexPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            Role role = user.getRole();
            return "redirect:" + ROLE_TO_HOME_PAGE.get(role);
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("user", new UserLoginForm());
        return "login";
    }

    @GetMapping("/sign-up")
    public String registrationPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String proceedRegistration(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                                      HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        session.invalidate();
        authService.register(user);
        return "redirect:/login";
    }

    private static Map<Role, String> initializeHomePages() {
        EnumMap<Role, String> map = new EnumMap<>(Role.class);
        map.put(Role.CLIENT, "/client");
        map.put(Role.ADMIN, "/admin");
        map.put(Role.WORKER, "/worker");
        return map;
    }
}
