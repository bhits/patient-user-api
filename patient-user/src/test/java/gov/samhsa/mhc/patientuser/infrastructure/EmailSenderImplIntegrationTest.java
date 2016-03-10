package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.PatientUserApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Conditional;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PatientUserApiApplication.class)
@WebIntegrationTest(randomPort = true)
@IfProfileValue(name = "test-groups", value = "integration-tests")
public class EmailSenderImplIntegrationTest {

    public static final String MOCK_TOKEN = "sampleToken";
    public static final String MOCK_RECIPIENT_FULL_NAME = "Firstname Lastname";

    @Value("${test.email}")
    private String emailAddress;

    @Autowired
    private EmailSender emailSender;

    @Before
    public void setup() {
        Assert.hasText(emailAddress, "emailAddress must have text");
    }

    @Test
    public void testSendEmailWithVerificationLink() throws Exception {
        emailSender.sendEmailWithVerificationLink(emailAddress, MOCK_TOKEN, MOCK_RECIPIENT_FULL_NAME);
    }

    @Test
    public void testSendEmailToConfirmVerification() throws Exception {
        emailSender.sendEmailToConfirmVerification(emailAddress, MOCK_RECIPIENT_FULL_NAME);
    }
}