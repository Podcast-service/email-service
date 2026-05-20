package com.emailService.consumer;

import com.emailService.dto.EmailRequest;
import com.emailService.exceptions.EmailSendingTimeoutException;
import com.emailService.exceptions.InvalidEmailRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    private final EmailHandler emailHandler;

    public EmailConsumer(EmailHandler emailHandler) {
        this.emailHandler = emailHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void listenerRabbit(EmailRequest request) {
        try {
            log.info("Received email request: type={}, to={}",
                    request != null ? request.type() : null,
                    request != null ? request.toEmail() : null);

            emailHandler.handle(request);

        } catch (InvalidEmailRequestException e) {
            log.warn("Invalid email request skipped: {}", e.getMessage());
        } catch (EmailSendingTimeoutException e) {
            log.error("Email sending timeout, retry skipped because delivery may have already happened: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process email request, message will be retried: {}", e.getMessage());
            throw e;
        }
    }
}
