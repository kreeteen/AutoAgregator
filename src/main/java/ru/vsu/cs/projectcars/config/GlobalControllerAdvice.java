package ru.vsu.cs.projectcars.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.model.RussianRegion;
import ru.vsu.cs.projectcars.repository.ProjectTagRepository;
import ru.vsu.cs.projectcars.repository.RussianRegionRepository;
import ru.vsu.cs.projectcars.security.JwtPrincipal;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final ProjectTagRepository tagRepository;
    private final RussianRegionRepository regionRepository;

    public GlobalControllerAdvice(ProjectTagRepository tagRepository,
                                  RussianRegionRepository regionRepository) {
        this.tagRepository = tagRepository;
        this.regionRepository = regionRepository;
    }

    @ModelAttribute("tags")
    public List<?> tags() { return tagRepository.findAll(); }

    @ModelAttribute("regions")
    public List<?> regions() { return regionRepository.findAllByOrderByNameAsc(); }

    @ModelAttribute("selectedRegion")
    public RussianRegion selectedRegion(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String regionIdParam = request.getParameter("regionId");
        if (regionIdParam != null && "GET".equalsIgnoreCase(request.getMethod())) {
            if (regionIdParam.isEmpty()) {
                session.removeAttribute("selectedRegionId");
                return null;
            }
            try {
                Integer regionId = Integer.valueOf(regionIdParam);
                session.setAttribute("selectedRegionId", regionId);
                return regionRepository.findById(regionId).orElse(null);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        Object attr = session.getAttribute("selectedRegionId");
        if (attr instanceof Integer id) {
            return regionRepository.findById(id).orElse(null);
        }
        return null;
    }

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            token.getToken();
        }
        return token;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtPrincipal;
    }

    @ModelAttribute("currentUser")
    public UserSession currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtPrincipal principal) {
            return new UserSession(principal.getUserId(), principal.getUsername(),
                    principal.getName(), null);
        }
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("isModerator")
    public boolean isModerator() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
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
