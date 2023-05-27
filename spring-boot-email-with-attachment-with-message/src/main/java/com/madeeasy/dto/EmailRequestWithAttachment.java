package com.madeeasy.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequestWithAttachment {
    private String fromEmail;
    private String toEmail;
    private String subject;
    private String text;
    private byte[] file;
}
