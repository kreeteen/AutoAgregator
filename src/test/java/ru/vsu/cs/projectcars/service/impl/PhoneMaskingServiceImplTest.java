package ru.vsu.cs.projectcars.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.projectcars.service.PhoneMaskingService;

import static org.junit.jupiter.api.Assertions.*;

class PhoneMaskingServiceImplTest {

    private final PhoneMaskingService service = new PhoneMaskingServiceImpl();

    @Test
    @DisplayName("Should mask phone number")
    void mask() {
        String masked = service.mask("+7 (916) 555-11-22");
        assertTrue(masked.contains("+7"));
        assertTrue(masked.contains("***"));
        assertFalse(masked.contains("555-11-22"));
    }

    @Test
    @DisplayName("Reveal should return original")
    void reveal() {
        assertEquals("+7 999 111-22-33", service.reveal("+7 999 111-22-33"));
    }

    @Test
    @DisplayName("Is masked should detect mask")
    void isMasked() {
        assertTrue(service.isMasked("+7 *** ** 22"));
        assertFalse(service.isMasked("+7 916 555-11-22"));
    }

    @Test
    @DisplayName("Null phone should return null")
    void maskNull() {
        assertNull(service.mask(null));
    }

    @Test
    @DisplayName("Short phone number should remain unchanged")
    void maskShortPhone() {
        assertEquals("123456", service.mask("123456"));
    }
}
