package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Mto> findByIncomingStockId(Long incomingStockId);

    @Query("SELECT m FROM Mto m JOIN m.description d WHERE d LIKE %:description%")
    List<Mto> findMtoByDescriptionContaining(@Param("description") String description);


    @Query("SELECT m FROM Mto m JOIN m.description d WHERE d LIKE %:description% AND m.locationName = :locationName AND m.status = :status")
    List<Mto> findMtoByDescriptionAndLocationAndStatus(
            @Param("description") String description,
            @Param("locationName") String locationName,
            @Param("status") String status);
    @Query("SELECT m FROM Mto m JOIN m.description d WHERE d LIKE %:description% AND m.locationName = :locationName AND m.transferDate = :transferDate AND m.status = :status")
    List<Mto> findMtoByDescriptionAndLocationAndTransferDateAndStatus(
            @Param("description") String description,
            @Param("locationName") String locationName,
            @Param("transferDate") LocalDate transferDate,
            @Param("status") String status);
    List<Mto> findMtoByStatus(String status);

    @Query("SELECT m FROM Mto m WHERE m.locationName = :locationName AND m.status = :status")
    List<Mto> findMtoByLocationAndStatus(
            @Param("locationName") String locationName,
            @Param("status") String status);

    List<Mto> findByReferenceNoContaining(String referenceNo);

    List<Mto> findByDescriptionAndLocationNameAndRepairServiceAndStatus(String description, String locationName, boolean repairService, String status);

    List<Mto> findByLocationNameAndRepairServiceAndStatus(String locationName, boolean repairService, String status);

    List<Mto> findByDescriptionAndRepairServiceAndStatus(String description, boolean repairService, String status);

    List<Mto> findByDescriptionAndLocationNameAndStatus(String description, String locationName, String status);

    List<Mto> findByLocationNameAndStatus(String locationName, String status);

    List<Mto> findByRepairServiceAndStatus(boolean repairService, String status);

    List<Mto> findByDescriptionAndStatus(String description, String status);

    List<Mto> findByRepairServiceAndStatusAndTransferDateBetween(boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByLocationNameAndStatusAndTransferDateBetween(String locationName, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndStatusAndTransferDateBetween(String description, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndLocationNameAndStatusAndTransferDateBetween(String description, String locationName, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByLocationNameAndRepairServiceAndStatusAndTransferDateBetween(String locationName, boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndLocationNameAndRepairServiceAndStatusAndTransferDateBetween(String description, String locationName, boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Mto> findByDescriptionAndRepairServiceAndStatusAndTransferDateBetween(String description, boolean repairService, String status, LocalDate startDate, LocalDate endDate);


    List<Mto> findByDescriptionAndLocationNameAndTransferDateAndStatus(String description, String locationName, LocalDate transferDate, String status);


    List<Mto> findByDescriptionAndTransferDateAndStatus(String description, LocalDate transferDate, String status);

    List<Mto> findByLocationNameAndTransferDateAndStatus(String locationName, LocalDate transferDate, String status);


    List<Mto> findByTransferDateAndStatus(LocalDate transferDate, String status);

}
