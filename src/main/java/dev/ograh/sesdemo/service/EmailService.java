package dev.ograh.sesdemo.service;

import dev.ograh.sesdemo.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @Async
    public CompletableFuture<String> sendEmail(EmailRequest request) {
        try {
            Destination.Builder destinationBuilder = Destination.builder()
                    .toAddresses(request.getTo());

            if (request.getCc() != null && !request.getCc().isEmpty()) {
                destinationBuilder.ccAddresses(request.getCc());
            }
            if (request.getBcc() != null && !request.getBcc().isEmpty()) {
                destinationBuilder.bccAddresses(request.getBcc());
            }

            Content subject = Content.builder()
                    .data(request.getSubject())
                    .charset("UTF-8")
                    .build();

            Body.Builder bodyBuilder = Body.builder();
            if (request.isHtml()) {
                bodyBuilder.html(Content.builder()
                        .data(request.getBody())
                        .charset("UTF-8")
                        .build());
            } else {
                bodyBuilder.text(Content.builder()
                        .data(request.getBody())
                        .charset("UTF-8")
                        .build());
            }

            Message message = Message.builder()
                    .subject(subject)
                    .body(bodyBuilder.build())
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(destinationBuilder.build())
                    .message(message)
                    .build();

            SendEmailResponse response = sesClient.sendEmail(emailRequest);

            log.info("Email sent successfully. MessageId: {}", response.messageId());
            return CompletableFuture.completedFuture(response.messageId());

        } catch (SesException e) {
            log.error("Failed to send email via SES: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Email sending failed: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public String sendRawEmail(List<String> toAddresses, String subject,
                               String htmlBody, String textBody) {
        try {
            Destination destination = Destination.builder()
                    .toAddresses(toAddresses)
                    .build();

            Body body = Body.builder()
                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                    .text(Content.builder().data(textBody).charset("UTF-8").build())
                    .build();

            Message message = Message.builder()
                    .subject(Content.builder().data(subject).charset("UTF-8").build())
                    .body(body)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(destination)
                    .message(message)
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("Multi-part email sent. MessageId: {}", response.messageId());
            return response.messageId();

        } catch (SesException e) {
            log.error("SES error: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Email failed: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}