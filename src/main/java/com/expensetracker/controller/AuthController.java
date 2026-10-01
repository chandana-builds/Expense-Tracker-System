package com.expensetracker.controller;

import com.expensetracker.dto.UserRegistrationDto;
import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    public static final String SESSION_USER_KEY = "LOGGED_IN_USER";

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER_KEY) != null) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            session.setAttribute(SESSION_USER_KEY, userOpt.get());
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid username/email or password.");
        model.addAttribute("username", username);
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER_KEY) != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("userDto", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            User newUser = userService.registerUser(userDto);
            session.setAttribute(SESSION_USER_KEY, newUser);
            return "redirect:/dashboard?registered=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
