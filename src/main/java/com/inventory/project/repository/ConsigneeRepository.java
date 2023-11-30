package com.inventory.project.repository;

import com.inventory.project.model.Consignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsigneeRepository extends JpaRepository<Consignee,Long> {
    boolean existsByName(String name);

    @Query("SELECT u FROM Consignee u WHERE u.id != :id AND u.name = :name")
    Consignee alreadyExists(Long id,String name);

    @Query("SELECT c FROM Consignee c WHERE c.phoneNumber= :phoneNumber AND c.email = :email")
    Consignee findByPhoneNumberAndEmail(String phoneNumber,String email);

    @Query("SELECT c FROM Consignee c WHERE c.phoneNumber = :phoneNumber AND c.email = :email AND c.id != :id")
    Consignee findByPhoneNumberAndEmailAndId(String phoneNumber, String email, Long id);


}
