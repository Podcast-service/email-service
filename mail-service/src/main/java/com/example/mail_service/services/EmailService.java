package com.example.mail_service.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.from}")
    private String from;

    private final Resend resend;

    @Autowired
    public EmailService(TemplateEngine templateEngine,
                        @Value("${spring.mail.password}") String key) {
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
            log.info("Email sent successfully to: {}, ID: {}", to, data.getId());

        } catch (ResendException e) {
            log.error("Resend API error for recipient: {}, msg: {}", to, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}", to, e);
            throw new RuntimeException(e);
        }
    }
}
