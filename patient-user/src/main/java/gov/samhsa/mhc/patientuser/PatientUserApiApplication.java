package gov.samhsa.mhc.patientuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
@EntityScan(basePackageClasses = {PatientUserApiApplication.class, Jsr310JpaConverters.class})
public class PatientUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientUserApiApplication.class, args);
    }
}
