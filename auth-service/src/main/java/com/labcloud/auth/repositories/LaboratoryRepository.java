package com.labcloud.auth.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.labcloud.auth.models.*;


@Repository 
public interface LaboratoryRepository extends JpaRepository<Laboratory, String>{
    
    //Buscas básicas
    Optional<Laboratory> findByTenantId(String tenantId);

    List<Laboratory> findByActiveTrue();

    List<Laboratory> findByNameContainingIgnoreCase(String name);


    boolean existsByTenantId(String tenantId);


    //Busca com relacionamento
    @Query("SELECT l FROM Laboratory l LEFT JOIN FETCH l.users WHERE l.id = :id")
    Optional<Laboratory> findByIdWithUsers(@Param("id") String id);

    //Contagem
    @Query("SELECT COUNT(u) FROM User u WHERE u.laboratory.id = :laboratoryId")
    long countUsersByLaboratoryId(@Param("laboratoryId") String laboratoryId);
}
