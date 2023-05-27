package com.madeeasy.controller;

import com.madeeasy.dto.EmailRequest;
import com.madeeasy.dto.EmailRequestWithAttachment;
import com.madeeasy.model.Files;
import com.madeeasy.service.EmailService;
import com.madeeasy.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/send-email")
public class EmailController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private EmailService emailService;

    @PostMapping(value = "/with-no-attachment")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailService.sendEmail(emailRequest);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders
                        .add("hello", "message sent"))
                .body("email successfully sent");
    }

    /**
     * This method is used to send an email with a single or multiple file as well as message from the user.
     * we are using @{@link RequestPart} to get value from Postman > body > form-data
     */
    @PostMapping(value = "/with-attachment")
    public ResponseEntity<String> uploadFiles(@RequestPart("fromEmail") String fromEmail,
                                              @RequestPart("toEmail") String toEmail,
                                              @RequestPart("subject") String subject,
                                              @RequestPart("text") String text,
                                              @RequestParam("file") MultipartFile[] files) throws Exception {
        List<Files> filesStored = new ArrayList<>();
        EmailRequestWithAttachment emailRequestWithAttachment = EmailRequestWithAttachment.builder()
                .fromEmail(fromEmail)
                .toEmail(toEmail)
                .subject(subject)
                .text(text)
                .build();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new FileNotFoundException("Failed to read file: " + file.getOriginalFilename());
            }
            Files storedFile = fileStorageService.storeListOfFiles(Collections.singletonList(file));
            filesStored.add(storedFile);
        }

        List<EmailRequestWithAttachment> emailRequestWithAttachments = filesStored.stream()
                .map(file -> {
                    return new EmailRequestWithAttachment(emailRequestWithAttachment.getFromEmail(),
                            emailRequestWithAttachment.getToEmail(),
                            emailRequestWithAttachment.getSubject(),
                            emailRequestWithAttachment.getText(),
                            file.getFileContent());
                }).collect(Collectors.toList());
        for (EmailRequestWithAttachment emailReqWithAttachment : emailRequestWithAttachments) {
            emailService.sendEmailWithAttachment(emailReqWithAttachment);
        }
        return ResponseEntity.ok()
                .header("email", "successfully sent")
                .body("successfully email sent with attachment");
    }

}
