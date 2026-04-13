package com.emailService.dto;

public record EmailRequest (
        RequestType type,
        String toEmail,
        String code
) {
}
