package gov.samhsa.c2s.patientuser.infrastructure;

import gov.samhsa.c2s.patientuser.config.EmailSenderProperties;
import gov.samhsa.c2s.patientuser.infrastructure.exception.EmailSenderException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
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

    private static final String PARAM_BRAND = "brand";

    @Autowired
    private EmailSenderProperties emailSenderProperties;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void sendEmailWithVerificationLink(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String emailToken, String recipientFullName) {
        Assert.hasText(emailToken, "emailToken must have text");
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");
        final String fragment = emailSenderProperties.getPpUiVerificationEmailTokenArgName() + "=" + emailToken;

        final String verificationUrl = toPPUIVerificationUri(xForwardedProto, xForwardedHost, xForwardedPort, fragment);
        final Context ctx = new Context();
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, verificationUrl);
        ctx.setVariable(PARAM_BRAND, emailSenderProperties.getBrand());
        ctx.setLocale(LocaleContextHolder.getLocale());// set locale to support multi-language
        sendEmail(ctx, email,
                PROP_EMAIL_VERIFICATION_LINK_SUBJECT,
                TEMPLATE_VERIFICATION_LINK_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                LocaleContextHolder.getLocale());
    }

    @Override
    public void sendEmailToConfirmVerification(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String recipientFullName) {
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");

        final Context ctx = new Context();
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, toPPUIBaseUri(xForwardedProto, xForwardedHost, xForwardedPort));
        ctx.setVariable(PARAM_BRAND, emailSenderProperties.getBrand());
        ctx.setLocale(LocaleContextHolder.getLocale());//should set Locale to support Multi-Language
        sendEmail(ctx, email,
                PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT,
                TEMPLATE_CONFIRM_VERIFICATION_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                LocaleContextHolder.getLocale());//
    }

    /*
    * used to send email with language requested from front-end
    * author: Wentao
    * */
    @Override
    public void sendEmailWithVerificationLinkAndLang(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String emailToken, String recipientFullName, String language) {
        Assert.hasText(emailToken, "emailToken must have text");
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");

        final Context ctx = new Context();
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, toPPUIBaseUri(xForwardedProto, xForwardedHost, xForwardedPort));
        ctx.setVariable(PARAM_BRAND, emailSenderProperties.getBrand());
        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }
        Locale locale = new Locale(language);
        ctx.setLocale(locale);// set locale to support multi-language
        sendEmail(ctx, email,
                PROP_EMAIL_VERIFICATION_LINK_SUBJECT,
                TEMPLATE_VERIFICATION_LINK_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                locale);
    }

    private void sendEmail(Context ctx, String email, String subjectPropKey, String templateName, String fromAddressPropKey, String fromPersonalPropKey, Locale locale) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, ENCODING);
            message.setSubject(messageSource.getMessage(subjectPropKey, null, locale));
            message.setTo(email);
            message.setFrom(messageSource.getMessage(fromAddressPropKey, null, locale),
                    messageSource.getMessage(fromPersonalPropKey, null, locale));
            final String htmlContent = templateEngine.process(templateName, ctx);
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSenderException(e);
        }
    }

    private String toPPUIBaseUri(String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        try {
            return createURIBuilder(xForwardedProto, xForwardedHost, xForwardedPort)
                    .setPath(emailSenderProperties.getPpUiRoute())
                    .build().toString();
        } catch (URISyntaxException e) {
            throw new EmailSenderException(e);
        }
    }

    private String toPPUIVerificationUri(String xForwardedProto, String xForwardedHost, int xForwardedPort, String fragment) {
        try {
            return createURIBuilder(xForwardedProto, xForwardedHost, xForwardedPort)
                    .setPath(emailSenderProperties.getPpUiRoute() + emailSenderProperties.getPpUiVerificationRelativePath())
                    .setFragment(fragment)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new EmailSenderException(e);
        }
    }

    private URIBuilder createURIBuilder(String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        final URIBuilder uriBuilder = new URIBuilder()
                .setScheme(xForwardedProto)
                .setHost(xForwardedHost);
        if (("http".equalsIgnoreCase(xForwardedProto) && xForwardedPort != 80) ||
                "https".equalsIgnoreCase(xForwardedProto) && xForwardedPort != 443) {
            uriBuilder.setPort(xForwardedPort);
        }
        return uriBuilder;
    }
}