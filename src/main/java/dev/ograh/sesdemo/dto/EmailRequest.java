package dev.ograh.sesdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid recipient email format")
    private String to;

    private List<String> cc;
    private List<String> bcc;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    @Builder.Default
    private boolean html = false;
}