package gov.samhsa.mhc.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScopeAssignmentRepository extends JpaRepository<UserScopeAssignment, Long> {
}
