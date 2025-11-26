package com.example.arkivebackend.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendAttachmentEmail(String to, String subject, String htmlBody, String attachmentPath) throws MessagingException;
    void sendAttachmentEmail(String to, String subject, String htmlBody) throws MessagingException;
}
