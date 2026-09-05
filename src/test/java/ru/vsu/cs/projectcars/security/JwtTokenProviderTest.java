package ru.vsu.cs.projectcars.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "ProjectCarsMarketplaceSecretKeyForJWTTokenSigning2024VeryLong!!";
    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);

    @Test
    void tokenShouldExposeUserDataAndRoles() {
        String token = provider.createToken(7, "admin@test.ru", "Admin", List.of("ADMIN", "MODERATOR"));

        assertTrue(provider.validate(token));
        assertEquals(7, provider.getUserId(token));
        assertEquals("admin@test.ru", provider.getEmail(token));
        assertEquals("Admin", provider.getName(token));
        assertEquals(List.of("ADMIN", "MODERATOR"), provider.getRoles(token));
    }

    @Test
    void tamperedTokenShouldBeRejected() {
        String token = provider.createToken(7, "user@test.ru", "User", List.of("USER"));

        assertFalse(provider.validate(token + "tampered"));
        assertThrows(RuntimeException.class, () -> provider.getUserId(token + "tampered"));
    }

    @Test
    void tokenCreatedWithAnotherSecretShouldBeRejected() {
        JwtTokenProvider anotherProvider = new JwtTokenProvider(
                "AnotherProjectCarsSecretKeyForJWTTokenSigning2024VeryLong!!", 60_000);
        String token = anotherProvider.createToken(7, "user@test.ru", "User", List.of("USER"));

        assertFalse(provider.validate(token));
    }
}
