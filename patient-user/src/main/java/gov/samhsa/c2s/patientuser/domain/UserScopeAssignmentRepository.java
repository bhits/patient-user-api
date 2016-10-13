package gov.samhsa.c2s.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScopeAssignmentRepository extends JpaRepository<UserScopeAssignment, Long> {
}