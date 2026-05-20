package com.emailService.consumer;

import com.emailService.dto.EmailRequest;
import com.emailService.exceptions.InvalidEmailRequestException;
import com.emailService.services.EmailService;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class EmailHandler {

    private final EmailService emailService;

    public EmailHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    public void handle(EmailRequest request) {
        validate(request);

        Context context = new Context();
        context.setVariable("code", request.code());

        switch (request.type()) {
            case EMAIL_VERIFY -> emailService.sendMail(
                    request.email(),
                    "Подтверждение почты",
                    "verification",
                    context
            );
            case PASSWORD_RESET -> emailService.sendMail(
                    request.email(),
                    "Сброс пароля",
                    "password-reset",
                    context
            );
        }
    }

    private void validate(EmailRequest request) {
        if (request == null) {
            throw new InvalidEmailRequestException("Request is null");
        }

        if (request.type() == null) {
            throw new InvalidEmailRequestException("Request type is null");
        }

        if (!isValidEmail(request.email())) {
            throw new InvalidEmailRequestException("Invalid email: " + request.email());
        }

        if (request.code() == null || request.code().isBlank()) {
            throw new InvalidEmailRequestException("Code is blank");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.indexOf("@") > 0 &&
                email.indexOf("@") < email.length() - 1;
    }
}
