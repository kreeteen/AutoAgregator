package ru.vsu.cs.projectcars.service.impl;

import org.springframework.stereotype.Service;
import ru.vsu.cs.projectcars.service.PhoneMaskingService;

@Service
public class PhoneMaskingServiceImpl implements PhoneMaskingService {

    @Override
    public String mask(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return phoneNumber;
        }
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        if (cleaned.length() <= 4) {
            return cleaned.substring(0, 1) + "***";
        }
        int visibleStart = Math.min(5, cleaned.length() - 4);
        return cleaned.substring(0, visibleStart) + " *** ** "
                + cleaned.substring(cleaned.length() - 2);
    }

    @Override
    public String reveal(String phoneNumber) {
        return phoneNumber;
    }

    @Override
    public boolean isMasked(String phoneNumber) {
        return phoneNumber != null && phoneNumber.contains("***");
    }
}
