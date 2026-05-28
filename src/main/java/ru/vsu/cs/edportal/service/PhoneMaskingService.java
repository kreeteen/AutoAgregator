package ru.vsu.cs.edportal.service;

public interface PhoneMaskingService {
    String mask(String phoneNumber);
    String reveal(String phoneNumber);
    boolean isMasked(String phoneNumber);
}
