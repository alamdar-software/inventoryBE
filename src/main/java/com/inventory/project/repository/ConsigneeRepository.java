package com.inventory.project.repository;

import com.inventory.project.model.Consignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsigneeRepository extends JpaRepository<Consignee,Long> {
    boolean existsByConsigneeName(String consigneeName);
    Consignee findTopByConsigneeName(String consigneeName);

    @Query("SELECT c FROM Consignee c WHERE c.consigneeName = :consigneeName")
    Consignee findFirstByConsigneeName(@Param("consigneeName") String consigneeName);


    @Query("SELECT u FROM Consignee u WHERE u.id != :id AND u.consigneeName = :name")
    Consignee alreadyExists(Long id, @Param("name") String name);


    @Query("SELECT c FROM Consignee c WHERE c.phoneNumber= :phoneNumber AND c.email = :email")
    Consignee findByPhoneNumberAndEmail(String phoneNumber,String email);

    @Query("SELECT c FROM Consignee c WHERE c.phoneNumber = :phoneNumber AND c.email = :email AND c.id != :id")
    Consignee findByPhoneNumberAndEmailAndId(String phoneNumber, String email, Long id);


    @Query("SELECT c FROM Consignee c JOIN c.location l WHERE l.locationName = :locationName")
    List<Consignee> findByLocationName(@Param("locationName") String locationName);

    List<Consignee> findByConsigneeName(String consigneeName);
}
