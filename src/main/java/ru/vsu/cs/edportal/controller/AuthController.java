package ru.vsu.cs.edportal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vsu.cs.edportal.dto.LoginRequest;
import ru.vsu.cs.edportal.dto.RegisterRequest;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.security.JwtAuthFilter;
import ru.vsu.cs.edportal.security.JwtTokenBlacklist;
import ru.vsu.cs.edportal.service.UserService;

import java.util.Optional;

@Controller
@Transactional
public class AuthController {

    private final UserService userService;
    private final JwtTokenBlacklist tokenBlacklist;

    public AuthController(UserService userService, JwtTokenBlacklist tokenBlacklist) {
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid LoginRequest request, BindingResult result,
                        HttpServletResponse response, Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }
        Optional<UserSession> auth = userService.authenticate(request);
        if (auth.isEmpty()) {
            model.addAttribute("error", "Неверный email или пароль");
            return "auth/login";
        }
        JwtAuthFilter.setJwtCookie(response, auth.get().getToken());
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterRequest request, BindingResult result,
                           HttpServletResponse response, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            UserSession session = userService.register(request);
            JwtAuthFilter.setJwtCookie(response, session.getToken());
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        JwtAuthFilter.clearJwtCookie(response);
        String token = JwtAuthFilter.extractTokenFromRequest(request);
        if (token != null) tokenBlacklist.revoke(token);
        return "redirect:/";
    }
}
