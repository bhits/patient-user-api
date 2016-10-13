package gov.samhsa.c2s.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findOneByType(UserTypeEnum type);
}