package gov.samhsa.mhc.patientuser.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by burcak.ulug on 3/1/2016.
 */
public interface UserTypeRepository extends JpaRepository<UserType, Long>{
    UserType findOneByType(UserTypeEnum type);
}
