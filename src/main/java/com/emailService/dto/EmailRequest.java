package com.emailService.dto;

public record EmailRequest (
        RequestType type,
        String email,
        String code
) {
}
