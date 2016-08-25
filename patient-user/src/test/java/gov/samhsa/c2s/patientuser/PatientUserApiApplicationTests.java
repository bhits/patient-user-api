package gov.samhsa.c2s.patientuser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PatientUserApiApplication.class)
@WebAppConfiguration
@IfProfileValue(name = "test-groups", value = "integration-tests")
public class PatientUserApiApplicationTests {

    @Test
    public void contextLoads() {
    }
}
