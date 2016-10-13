package gov.samhsa.c2s.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCreationRepository extends JpaRepository<UserCreation, Long> {
    Optional<UserCreation> findOneByEmailToken(String emailToken);

    Optional<UserCreation> findOneByPatientId(Long patientId);

    Optional<UserCreation> findOneByEmailTokenAndVerificationCode(String emailToken, String verificationCode);

    List<UserCreation> findAll();
}