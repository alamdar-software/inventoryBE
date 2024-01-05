package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface MtoRepository extends JpaRepository<Mto,Long> {

//    List<Mto> findByItemAndLocationName(String item, String locationName);
//
//    List<Mto> findByItemAndLocationNameAndTransferDate(String item, String locationName, LocalDate transferDate);
//
//    List<Mto> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);
//
//    List<Mto> findByTransferDate(LocalDate transferDate);
//
//    List<Mto> findByLocationName(String locationName);
//
//    List<Mto> findByItem(String item);

    List<Mto> findByDescriptionAndLocationNameAndTransferDate(
            String description, String locationName, LocalDate transferDate);

    List<Mto> findByDescriptionAndLocationName(String description, String locationName);

    List<Mto> findByDescription(String description);

    List<Mto> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);

    List<Mto> findByLocationName(String locationName);

    List<Mto> findByTransferDate(LocalDate transferDate);
}
