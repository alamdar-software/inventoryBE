package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CiplRepository extends JpaRepository<Cipl,Long> {
    List<Cipl> findByItemInAndLocationNameAndTransferDate(List<String> item, String locationName, LocalDate transferDate);


    List<Cipl> findByLocationNameAndTransferType(String locationName, String transferType);

    List<Cipl> findByLocationNameOrderByReferenceNoDesc(String locationName);
}
