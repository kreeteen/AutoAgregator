package ru.vsu.cs.projectcars.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vsu.cs.projectcars.dto.LoginRequest;
import ru.vsu.cs.projectcars.dto.RegisterRequest;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.service.UserService;

import java.util.Optional;

@Controller
@Transactional
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(Authentication authentication,
                            @RequestParam(value = "redirect", defaultValue = "") String redirect,
                            HttpServletRequest httpRequest, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + (redirect.isBlank() ? "/cars" : redirect);
        }
        String target = redirect;
        if (target.isBlank()) {
            String referer = httpRequest.getHeader("Referer");
            if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
                target = referer;
            }
        }
        model.addAttribute("redirectUrl", target.isBlank() ? "/cars" : target);
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(Authentication authentication, @Valid LoginRequest request, BindingResult result,
                        HttpServletResponse response,
                        @RequestParam(value = "redirect", defaultValue = "/cars") String redirect,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + redirect;
        }
        if (result.hasErrors()) {
            return "auth/login";
        }
        Optional<UserSession> auth = userService.authenticate(request);
        if (auth.isEmpty()) {
            model.addAttribute("error", "Неверный email или пароль, или учётная запись заблокирована");
            return "auth/login";
        }
        setJwtCookie(response, auth.get().getToken());
        return "redirect:" + redirect;
    }

    @GetMapping("/register")
    public String registerForm(Authentication authentication,
                               @RequestParam(value = "redirect", defaultValue = "") String redirect,
                               HttpServletRequest httpRequest, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + (redirect.isBlank() ? "/cars" : redirect);
        }
        String target = redirect;
        if (target.isBlank()) {
            String referer = httpRequest.getHeader("Referer");
            if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
                target = referer;
            }
        }
        model.addAttribute("redirectUrl", target.isBlank() ? "/cars" : target);
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(Authentication authentication, @Valid RegisterRequest request, BindingResult result,
                           HttpServletResponse response,
                           @RequestParam(value = "redirect", defaultValue = "/cars") String redirect,
                           Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + redirect;
        }
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            UserSession session = userService.register(request);
            setJwtCookie(response, session.getToken());
            return "redirect:" + redirect;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(86400)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
