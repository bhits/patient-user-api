package gov.samhsa.mhc.patientuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class PatientUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientUserApiApplication.class, args);
    }
}
