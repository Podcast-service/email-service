package com.emailService.services;

import com.emailService.exceptions.EmailSendingException;
import com.emailService.exceptions.EmailSendingTimeoutException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.SocketTimeoutException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final TemplateEngine templateEngine;
    private final Resend resend;

    @Value("${spring.mail.from}")
    private String from;

    public EmailService(
            TemplateEngine templateEngine,
            @Value("${spring.mail.password}") String key
    ) {
        this.templateEngine = templateEngine;
        this.resend = new Resend(key);
    }

    public void sendMail(String to, String subject, String template, Context context) {
        try {
            log.info("Sending email to {}", to);

            String htmlContent = templateEngine.process(template, context);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            log.info("Email sent successfully to {}, id={}", to, data.getId());

        } catch (RuntimeException e) {
            if (hasSocketTimeout(e)) {
                log.error("Timeout while sending email to {}. Delivery status is unknown", to);
                throw new EmailSendingTimeoutException("Email sending timed out", e);
            }

            log.error("Unexpected sending error for {}: {}", to, e.getMessage());
            throw new EmailSendingException("Unexpected email sending error", e);

        } catch (ResendException e) {
            log.error("Resend API error for {}: {}", to, e.getMessage());
            throw new EmailSendingException("Failed to send email via Resend", e);
        }
    }

    private boolean hasSocketTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
