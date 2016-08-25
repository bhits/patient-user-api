package gov.samhsa.c2s.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScopeRepository extends JpaRepository<Scope, Long>{

    Scope findByScope(String scope);
}
