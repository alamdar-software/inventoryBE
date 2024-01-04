package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CiplRepository extends JpaRepository<Cipl,Long> {
    List<Cipl> findByItemAndLocationNameAndTransferDate(String item, String locationName, LocalDate transferDate);

    List<Cipl> findByItemAndLocationName(String item, String locationName);

    List<Cipl> findByItem(String item);

    List<Cipl> findByLocationName(String locationName);

    List<Cipl> findByTransferDate(LocalDate transferDate);

    List<Cipl> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);
}
