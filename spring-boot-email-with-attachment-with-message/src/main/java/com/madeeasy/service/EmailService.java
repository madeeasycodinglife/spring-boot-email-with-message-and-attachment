package com.madeeasy.service;


import com.madeeasy.dto.EmailRequest;
import com.madeeasy.dto.EmailRequestWithAttachment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(EmailRequest emailRequest){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailRequest.getFromEmail());
        simpleMailMessage.setTo(emailRequest.getToEmail());
        simpleMailMessage.setSubject(emailRequest.getSubject());
        simpleMailMessage.setText(emailRequest.getText());
        javaMailSender.send(simpleMailMessage);
    }

    public void sendEmailWithAttachment(EmailRequestWithAttachment emailRequestWithAttachment) throws MessagingException, IOException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom("no-replay@myhost.com");
        mimeMessageHelper.setTo(emailRequestWithAttachment.getToEmail());
        mimeMessageHelper.setSubject(emailRequestWithAttachment.getSubject());
        mimeMessageHelper.setText(emailRequestWithAttachment.getText());

        // Create a temporary file from the byte[] data
        File tempFile = File.createTempFile("attachment", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(emailRequestWithAttachment.getFile());
        }

        FileSystemResource file = new FileSystemResource(tempFile);
        mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

        javaMailSender.send(message);

        // Delete the temporary file
        tempFile.delete();
    }
}
