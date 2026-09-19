package com.labcloud.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.models.User;

@Repository 
public interface UserRepository extends JpaRepository<User, String>{

    //Buscas básicas
    Optional<User> findByEmail (String email);
    
    boolean existsByEmail(String email);

    List<User> findByTenantId(String tenantId);

    List<User> findByLaboratoryId(String LaboratoryId);

    List<User> findByLaboratoryIdAndActiveTrue(String laboratoryId);

    List<User> findByLaboratoryIdAndRole(String laboratoryId, UserRole role);

    //Buscas com relacionamento
    @Query("SELECT u FROM User u JOIN FETCH u.laboratory WHERE u.email = :email")
    Optional<User> findByEmailWithLaboratory(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.laboratory WHERE u.id = :id AND u.tenantId = :tenantId")
    Optional<User> findByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

    //Contagem
    @Query("SELECT COUNT(u) FROM User u WHERE u.laboratory.id = :laboratoryId")
    long countByLaboratoryId(@Param("laboratoryId") String laboratoryId);

}
