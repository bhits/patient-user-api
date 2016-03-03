package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.infrastructure.exception.EmailSenderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderImpl implements EmailSender {

    @Value("${mhc.apis.pp-ui}")
    private String ppUIBaseUri;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendEmailWithVerificationLink(String email, String emailToken, String recipientFullName) {
        Assert.hasText(emailToken, "emailToken must have text");
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");
        try {
            Context ctx = new Context();
            ctx.setVariable("recipientName", recipientFullName);
            ctx.setVariable("linkUrl", ppUIBaseUri + "/fe/verify#emailToken=" + emailToken);
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Set up your My Health Compass account");
            message.setTo(email);
            final String htmlContent = templateEngine.process("email-with-verification-link", ctx);
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSenderException(e);
        }
    }
}
