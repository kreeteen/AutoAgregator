package ru.vsu.cs.projectcars.service;

public interface PhoneMaskingService {
    String mask(String phoneNumber);
    String reveal(String phoneNumber);
    boolean isMasked(String phoneNumber);
}
