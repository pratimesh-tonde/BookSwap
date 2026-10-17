package com.bookexchange.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Sends plain-text emails through JavaMailSender. Does nothing unless app.mail.enabled=true and SMTP is configured. */
@Slf4j
@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> senderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from:no-reply@bookexchange.local}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> senderProvider) {
        this.senderProvider = senderProvider;
    }

    @Async
    public void send(String to, String subject, String body) {
        if (!enabled || to == null) {
            return;
        }
        JavaMailSender sender = senderProvider.getIfAvailable();
        if (sender == null) {
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            sender.send(mail);
        } catch (Exception e) {
            log.warn("Could not send email to {}: {}", to, e.getMessage());
        }
    }
}
