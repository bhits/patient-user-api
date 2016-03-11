package gov.samhsa.mhc.patientuser.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(TemplateEngine.class)
public class EmailSenderImplTest {

    private static final String ppUIBaseUri = "ppUIBaseUri";
    private static final String ppUIVerificationRelativePath = "ppUIVerificationRelativePath";
    private static final String subject = "subject";
    private static final String htmlContent = "htmlContent";
    @Mock
    private JavaMailSender javaMailSenderMock;
    private TemplateEngine templateEngineMock;
    @Mock
    private MessageSource messageSourceMock;
    @InjectMocks
    private EmailSenderImpl sut;

    @Before
    public void setup() {
        templateEngineMock = PowerMockito.mock(TemplateEngine.class);
        PowerMockito.doReturn(htmlContent).when(templateEngineMock).process(anyString(), any(Context.class));
        ReflectionTestUtils.setField(sut, "templateEngine", templateEngineMock);
        ReflectionTestUtils.setField(sut, ppUIBaseUri, ppUIBaseUri);
        ReflectionTestUtils.setField(sut, ppUIVerificationRelativePath, ppUIVerificationRelativePath);
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(javaMailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);
        when(messageSourceMock.getMessage(eq(ReflectionTestUtils.getField(sut, "PROP_EMAIL_VERIFICATION_LINK_SUBJECT").toString()), eq(null), any(Locale.class))).thenReturn(subject);
        when(messageSourceMock.getMessage(eq(ReflectionTestUtils.getField(sut, "PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT").toString()), eq(null), any(Locale.class))).thenReturn(subject);
    }

    @Test
    public void testSendEmailWithVerificationLink() throws Exception {
        // Arrange
        final String email = "email";
        final String emailToken = "emailToken";
        final String recipientFullName = "recipientFullName";

        // Act
        sut.sendEmailWithVerificationLink(email, emailToken, recipientFullName);

        // Assert
        verify(templateEngineMock, times(1)).process(anyString(), any(Context.class));
        verify(javaMailSenderMock, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmailToConfirmVerification() throws Exception {
        // Arrange
        final String email = "email";
        final String recipientFullName = "recipientFullName";

        // Act
        sut.sendEmailToConfirmVerification(email, recipientFullName);

        // Assert
        verify(templateEngineMock, times(1)).process(anyString(), any(Context.class));
        verify(javaMailSenderMock, times(1)).send(any(MimeMessage.class));
    }
}