package com.example.mail_service.controllers;

import com.example.mail_service.services.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;
    private final ObjectMapper mapper;

    @Autowired
    public EmailController(EmailService emailService, ObjectMapper mapper) {
        this.emailService = emailService;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listenerRabbit(String message) {

        if (message == null || message.isBlank()) {
            log.error("Receiveds empty message");
            return;
        }

        try {
            JsonNode request = mapper.readTree(message);

            if (!request.isObject()) {
                log.error("Invalid JSON: expected object, got {}", request.getNodeType());
                return;
            }

            String type = request.path("type").asText(null);
            String toEmail = request.path("toEmail").asText(null);
            String link = request.path("link").asText(null);

            if (type == null || type.isBlank()) {
                log.error("Invalid type");
                return;
            }

            if (!isValidEmail(toEmail)) {
                log.error("Invalid email: {}", toEmail);
                return;
            }

            if (link == null || link.isBlank()) {
                log.error("Invalid link");
                return;
            }

            log.info("Processing email: {} -> {}", type, toEmail);

            Context context = new Context();

            switch (type) {
                case "EMAIL_VERIFY" -> {
                    context.setVariable("verificationLink", link);
                    emailService.sendMail(toEmail, "Подтверждение почты", "verification", context);
                }
                case "PASSWORD_RESET" -> {
                    context.setVariable("resetLink", link);
                    emailService.sendMail(toEmail, "Сброс пароля", "password-reset", context);
                }
                default -> log.error("Unknown type: {}", type);
            }

        } catch (JsonProcessingException ex) {
            log.error("Error in JSON processing message: ", ex);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
            throw e;
        }
    }

    private boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.indexOf("@") > 0 &&
                email.indexOf("@") < email.length() - 1;
    }
}