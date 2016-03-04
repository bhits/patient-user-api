package gov.samhsa.mhc.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreationRepository extends JpaRepository<UserCreation, Long> {
    Optional<UserCreation> findOneByEmailToken(String emailToken);

    Optional<UserCreation> findOneByPatientId(Long patientId);

    Optional<UserCreation> findOneByEmailTokenAndVerificationCode(String emailToken, String verificationCode);
}
