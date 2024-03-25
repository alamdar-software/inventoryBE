package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
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

    List<Mto> findByTransferDateBetween(LocalDate startDate, LocalDate endDate);

    List<Mto> findMtoEntitiesByRepairService(boolean repairService);


  

    List<Mto> findByLocationNameAndTransferDateBetween(String locationName, LocalDate startDate, LocalDate endDate);


    List<Mto> findByDescriptionAndLocationNameAndRepairService(String description, String locationName, boolean repairService);

    List<Mto> findByDescriptionAndRepairService(String description, boolean repairService);

    List<Mto> findByLocationNameAndRepairService(String locationName, boolean repairService);

    List<Mto> findByRepairService(boolean repairService);

    List<Mto> findByDescriptionAndLocationNameAndRepairServiceAndTransferDateBetween(String description, String locationName, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndRepairServiceAndTransferDateBetween(String description, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndTransferDateBetween(String description, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndLocationNameAndTransferDateBetween(String description, String locationName, LocalDate startDate, LocalDate endDate);

    List<Mto> findByRepairServiceAndTransferDateBetween(boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Mto> findByLocationNameAndRepairServiceAndTransferDateBetween(String locationName, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Mto> findByTransferDateBetweenAndDescription(LocalDate startDate, LocalDate endDate, String description);

    List<Mto> findByTransferDateBetweenAndLocationName(LocalDate startDate, LocalDate endDate, String locationName);

    List<Mto> findByLocationNameAndTransferDateBetweenAndDescription(String locationName, LocalDate startDate, LocalDate endDate, String description);
    @Query("SELECT m FROM Mto m WHERE m.locationName = :locationName AND :description MEMBER OF m.description")
    List<Mto> findByLocationNameAndDescription(String locationName, String description);

    List<Mto> findByStatus(String created);

}
