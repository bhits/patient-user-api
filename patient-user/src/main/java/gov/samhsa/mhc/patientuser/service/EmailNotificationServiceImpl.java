package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.exception.EmailNotificationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    @Value("${mhc.apis.pp-ui}")
    private String ppUIBaseUri;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendEmailWithVerificationLink(String emailToken, PatientDto patientDto) {
        try {
            Context ctx = new Context();
            ctx.setVariable("recipientName", patientDto.getFirstName() + " " + patientDto.getLastName());
            ctx.setVariable("linkUrl", ppUIBaseUri + "/fe/verify#emailToken=" + emailToken);
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Set up your My Health Compass account");
            message.setTo(patientDto.getEmail());
            final String htmlContent = templateEngine.process("verification-new-accountsignup-template", ctx);
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailNotificationServiceException(e);
        }
    }
}
