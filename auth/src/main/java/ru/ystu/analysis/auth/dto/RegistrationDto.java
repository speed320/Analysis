package ru.ystu.analysis.auth.dto;

public record RegistrationDto(
        String username,
        String password,
        String email
) {
}
