package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl implements EmailService {
    JavaMailSender mailSender;

    /**
     * Gửi một email văn bản đơn giản.
     * @param to Địa chỉ email người nhận.
     * @param subject Tiêu đề email.
     * @param body Nội dung email.
     */
    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    /**
     * Gửi một email với tệp đính kèm và nội dung HTML.
     * @param to Địa chỉ email người nhận.
     * @param subject Tiêu đề email.
     * @param htmlBody Nội dung email.
     * @param attachmentPath Đường dẫn đến tệp đính kèm (Optional).
     */
    @Async
    @Override
    public void sendAttachmentEmail(String to, String subject, String htmlBody, String attachmentPath) 
        throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        if (attachmentPath != null) {
            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(file.getFilename(), file);
        }
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }
    @Async
    @Override
    public void sendAttachmentEmail(String to, String subject, String htmlBody) 
        throws MessagingException {
        sendAttachmentEmail(to, subject, htmlBody, null);
    }
}
