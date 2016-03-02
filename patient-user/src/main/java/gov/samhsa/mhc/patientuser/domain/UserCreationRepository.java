package gov.samhsa.mhc.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreationRepository extends JpaRepository<UserCreation, Long> {
    UserCreation findOneByEmailToken(String emailToken);
    UserCreation findOneByPatientId(Long patientId);
    default Optional<UserCreation> findOneByPatientIdAsOptional(Long patientId){
        return Optional.ofNullable(findOneByPatientId(patientId));
    }
}
