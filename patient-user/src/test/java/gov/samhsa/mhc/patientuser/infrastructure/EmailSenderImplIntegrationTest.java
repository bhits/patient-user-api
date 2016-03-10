package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.PatientUserApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PatientUserApiApplication.class)
@WebIntegrationTest(randomPort = true)
@IfProfileValue(name = "test-groups", value = "integration-tests")
public class EmailSenderImplIntegrationTest {

    public static final String MOCK_TOKEN = "sampleToken";
    public static final String MOCK_RECIPIENT_FULL_NAME = "Firstname Lastname";

    @Value("${test.emails}")
    private String emailAddressesString;
    private List<String> emailAddresses;

    @Autowired
    private EmailSender emailSender;

    @Before
    public void setup() {
        Assert.hasText(emailAddressesString, "emailAddressesString must have text");
        emailAddresses = Arrays.stream(emailAddressesString.split("\\,")).collect(toList());
        Assert.notEmpty(emailAddresses, "emailAddresses must have at least one address");
        emailAddresses.forEach(address -> Assert.hasText(address, "Each email address must have some text"));
    }

    @Test
    public void testSendEmailWithVerificationLink() throws Exception {
        emailAddresses.forEach(address -> emailSender.sendEmailWithVerificationLink(address, MOCK_TOKEN, MOCK_RECIPIENT_FULL_NAME));
    }

    @Test
    public void testSendEmailToConfirmVerification() throws Exception {
        emailAddresses.forEach(address -> emailSender.sendEmailToConfirmVerification(address, MOCK_RECIPIENT_FULL_NAME));
    }
}