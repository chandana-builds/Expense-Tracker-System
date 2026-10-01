package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    @Autowired
    protected UserService userService;

    protected User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute(AuthController.SESSION_USER_KEY);
        if (user == null) {
            // Default to demo user for convenience if session expired
            user = userService.findByUsername("demo").orElse(null);
            if (user != null) {
                session.setAttribute(AuthController.SESSION_USER_KEY, user);
            }
        }
        return user;
    }
}
