package dev.ograh.sesdemo.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ResendService {

    private static final Logger log = LoggerFactory.getLogger(ResendService.class);
    @Value("${resend.api-key}")
    private String resendApiKey;

    @Async
    public void sendEmail() {
        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Spring Boot Resend <noreply@ograh.xyz>")
                .to("ograhbernard@gmail.com", "benclanks@gmail.com")
                .subject("Hello From Spring Boot + Resend")
                .html("<h1>This is a test email sent from Spring Boot via Resend</h1>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info(data.getId());
        } catch (ResendException e) {
            log.error("Resend error: {}", e.getMessage());
        }
    }

}