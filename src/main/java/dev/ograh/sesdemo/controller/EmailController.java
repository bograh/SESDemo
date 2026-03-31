package dev.ograh.sesdemo.controller;

import dev.ograh.sesdemo.dto.EmailRequest;
import dev.ograh.sesdemo.service.EmailService;
import dev.ograh.sesdemo.service.ResendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final ResendService resendService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailRequest request) {
        String messageId = emailService.sendEmail(request).join();
        resendService.sendEmail();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "messageId", messageId,
                "message", "Email sent successfully"
        ));
    }

    @PostMapping("/send-multipart")
    public ResponseEntity<Map<String, String>> sendMultipartEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String htmlBody,
            @RequestParam String textBody) {

        String messageId = emailService.sendRawEmail(
                java.util.List.of(to), subject, htmlBody, textBody);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "messageId", messageId
        ));
    }
}