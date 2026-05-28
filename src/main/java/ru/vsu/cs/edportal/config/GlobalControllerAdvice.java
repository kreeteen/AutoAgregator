package ru.vsu.cs.edportal.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.repository.ProjectTagRepository;
import ru.vsu.cs.edportal.repository.RussianRegionRepository;
import ru.vsu.cs.edportal.security.CsrfManager;
import ru.vsu.cs.edportal.security.UserContext;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final ProjectTagRepository tagRepository;
    private final RussianRegionRepository regionRepository;
    private final UserContext userContext;
    private final CsrfManager csrfManager;

    public GlobalControllerAdvice(ProjectTagRepository tagRepository,
                                  RussianRegionRepository regionRepository,
                                  UserContext userContext,
                                  CsrfManager csrfManager) {
        this.tagRepository = tagRepository;
        this.regionRepository = regionRepository;
        this.userContext = userContext;
        this.csrfManager = csrfManager;
    }

    @ModelAttribute("tags")
    public List<?> tags() { return tagRepository.findAll(); }

    @ModelAttribute("regions")
    public List<?> regions() { return regionRepository.findAllByOrderByNameAsc(); }

    @ModelAttribute("_csrf")
    public String csrfToken(HttpServletRequest request) {
        return csrfManager.getToken(request);
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() { return userContext.isAuthenticated(); }

    @ModelAttribute("currentUser")
    public UserSession currentUser() {
        if (!userContext.isAuthenticated()) return null;
        return new UserSession(userContext.getUserId(), userContext.getEmail(), userContext.getName(), null);
    }

    @ModelAttribute("priceOptions")
    public List<Integer> priceOptions() {
        return List.of(50000, 100000, 150000, 200000, 300000, 500000, 800000,
                1000000, 1500000, 2000000, 3000000, 5000000, 8000000, 10000000);
    }

    @ModelAttribute("kmOptions")
    public List<Integer> kmOptions() {
        return List.of(10000, 20000, 30000, 50000, 80000, 100000, 150000, 200000, 300000);
    }

    @ModelAttribute("hourOptions")
    public List<Integer> hourOptions() { return List.of(500, 1000, 2000, 3000, 5000, 8000, 10000); }

    @ModelAttribute("bodyTypes")
    public List<String> bodyTypes() {
        return List.of("Седан", "Хетчбэк", "Универсал", "Купе", "Кабриолет",
                "Внедорожник", "Пикап", "Минивэн", "Лифтбэк", "Фургон");
    }

    @ModelAttribute("engineTypes")
    public List<String> engineTypes() { return List.of("Бензин", "Дизель", "Гибрид", "Электро"); }

    @ModelAttribute("engineDisplacementOptions")
    public List<Double> engineDisplacementOptions() {
        List<Double> options = new ArrayList<>();
        for (double d = 0.1; d <= 4.0; d += 0.1) options.add(Math.round(d * 10.0) / 10.0);
        for (double d = 4.5; d <= 10.0; d += 0.5) options.add(d);
        return options;
    }

    @ModelAttribute("powerOptions")
    public List<Integer> powerOptions() {
        List<Integer> options = new ArrayList<>();
        for (int i = 50; i <= 1000; i += 10) options.add(i);
        return options;
    }
}
