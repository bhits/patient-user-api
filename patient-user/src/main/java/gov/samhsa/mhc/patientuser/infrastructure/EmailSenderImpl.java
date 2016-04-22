package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.infrastructure.exception.EmailSenderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class EmailSenderImpl implements EmailSender {

    // Verification link
    private static final String TEMPLATE_VERIFICATION_LINK_EMAIL = "verification-link-email";
    private static final String PROP_EMAIL_VERIFICATION_LINK_SUBJECT = "email.verificationLink.subject";

    // Confirm verification
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT = "email.confirmVerification.subject";
    private static final String TEMPLATE_CONFIRM_VERIFICATION_EMAIL = "confirm-verification-email";
    private static final String PROP_EMAIL_FROM_ADDRESS = "email.from.address";
    private static final String PROP_EMAIL_FROM_PERSONAL = "email.from.personal";



    private static final String ENCODING = StandardCharsets.UTF_8.toString();

    private static final String PARAM_RECIPIENT_NAME = "recipientName";
    private static final String PARAM_LINK_URL = "linkUrl";

    @Value("${mhc.apis.pp-ui}")
    private String ppUIBaseUri;

    @Value("${mhc.patient-user.config.pp-ui-verification-relative-path}")
    private String ppUIVerificationRelativePath;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void sendEmailWithVerificationLink(String email, String emailToken, String recipientFullName) {
        Assert.hasText(emailToken, "emailToken must have text");
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");

        final String verificationUrl = ppUIBaseUri + ppUIVerificationRelativePath + emailToken;
        final Context ctx = new Context();
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, verificationUrl);
        sendEmail(ctx, email,
                PROP_EMAIL_VERIFICATION_LINK_SUBJECT,
                TEMPLATE_VERIFICATION_LINK_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                Locale.getDefault());
    }

    @Override
    public void sendEmailToConfirmVerification(String email, String recipientFullName) {
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");

        final Context ctx = new Context();
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, ppUIBaseUri);
        sendEmail(ctx, email,
                PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT,
                TEMPLATE_CONFIRM_VERIFICATION_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                Locale.getDefault());
    }

    private void sendEmail(Context ctx, String email, String subjectPropKey, String templateName,String fromAddressPropKey,String fromPersonalPropKey, Locale locale) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, ENCODING);
            message.setSubject(messageSource.getMessage(subjectPropKey, null, locale));
            message.setTo(email);
            //message.setFrom("noreply@mhc.com", "MHC");
            message.setFrom(messageSource.getMessage(fromAddressPropKey, null, locale), messageSource.getMessage(fromPersonalPropKey, null, locale));
            final String htmlContent = templateEngine.process(templateName, ctx);
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSenderException(e);
        }
    }
}
