package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

 
//
List<Cipl> findByItemAndRepairServiceAndTransferDateBetween(String item, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByShipperNameAndRepairServiceAndTransferDateBetween(String shipperName, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndShipperNameAndTransferDateBetween(String item, String shipperName, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndTransferDateBetween(String item, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByShipperNameAndTransferDateBetween(String shipperName, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByRepairServiceAndTransferDateBetween(boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByTransferDateBetween(LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndRepairService(String item, boolean repairService);

    List<Cipl> findByShipperNameAndRepairService(String shipperName, boolean repairService);

    List<Cipl> findByRepairService(boolean repairService);


    List<Cipl> findByConsigneeNameAndTransferDateBetween(String consigneeName, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndShipperNameAndConsigneeNameAndRepairService(String item, String shipperName, String consigneeName, boolean repairService);

    List<Cipl> findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndTransferDateBetween(String item, String shipperName, String consigneeName, boolean repairService, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByConsigneeName(String consigneeName);

    List<Cipl> findByShipperName(String shipperName);

    List<Cipl> findByLocationNameAndItemAndTransferDateBetween(String locationName, String item, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByTransferDateBetweenAndLocationName(LocalDate startDate, LocalDate endDate, String locationName);

    List<Cipl> findByTransferDateBetweenAndItem(LocalDate startDate, LocalDate endDate, String item);

    List<Cipl> findByLocationNameAndTransferDateBetweenAndItem(String locationName, LocalDate startDate, LocalDate endDate, String item);

    List<Cipl> findByStatus(String created);

    List<Cipl> findByStatusIgnoreCase(String status);
    List<Cipl> findByItemContainingIgnoreCaseAndStatusIgnoreCase(String item, String status);
    List<Cipl> findByLocationNameIgnoreCaseAndStatusIgnoreCase(String locationName, String status);
    List<Cipl> findByTransferDateAndStatusIgnoreCase(LocalDate transferDate, String status);
    List<Cipl> findByItemContainingIgnoreCaseAndLocationNameIgnoreCaseAndStatusIgnoreCase(String item, String locationName, String status);
    List<Cipl> findByItemContainingIgnoreCaseAndLocationNameIgnoreCaseAndTransferDateAndStatusIgnoreCase(String item, String locationName, LocalDate transferDate, String status);

    List<Cipl> findByItemAndLocationNameAndTransferDateAndStatusAndReferenceNoContaining(String item, String locationName, LocalDate transferDate, String status, String referenceNumber);

    List<Cipl> findByItemAndLocationNameAndStatusAndReferenceNoContaining(String item, String locationName, String status, String referenceNumber);

    List<Cipl> findByItemAndStatusAndReferenceNoContaining(String item, String status, String referenceNumber);

    List<Cipl> findByLocationNameAndTransferDateAndStatusAndReferenceNoContaining(String locationName, LocalDate transferDate, String status, String referenceNumber);

    List<Cipl> findByLocationNameAndStatusAndReferenceNoContaining(String locationName, String status, String referenceNumber);

    List<Cipl> findByItemAndReferenceNoContaining(String item, String referenceNumber);

    List<Cipl> findByTransferDateAndStatusAndReferenceNoContaining(LocalDate transferDate, String status, String referenceNumber);

    List<Cipl> findByLocationNameAndReferenceNoContaining(String locationName, String referenceNumber);

    List<Cipl> findByTransferDateAndReferenceNoContaining(LocalDate transferDate, String referenceNumber);

    List<Cipl> findByStatusAndReferenceNoContaining(String status, String referenceNumber);

    List<Cipl> findByReferenceNoContaining(String referenceNumber);
    @Query("SELECT m FROM Cipl m JOIN m.item d WHERE d LIKE %:item%")
    List<Cipl> findCiplByDescriptionContaining(@Param("item") String item);


    List<Cipl> findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndStatus(
            String item, String shipperName, String consigneeName, boolean repairService, String status);

    List<Cipl> findByItemAndRepairServiceAndStatus(String item, boolean repairService, String status);

    List<Cipl> findByShipperNameAndRepairServiceAndStatus(String shipperName, boolean repairService, String status);

    List<Cipl> findByItemAndShipperNameAndStatus(String item, String shipperName, String status);

    List<Cipl> findByItemAndStatus(String item, String status);

    List<Cipl> findByShipperNameAndStatus(String shipperName, String status);

    List<Cipl> findByConsigneeNameAndStatus(String consigneeName, String status);

    List<Cipl> findByRepairServiceAndStatus(boolean repairService, String status);


    List<Cipl> findByStatusAndTransferDateBetween(String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndStatusAndTransferDateBetween(String item, String shipperName, String consigneeName, boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByRepairServiceAndStatusAndTransferDateBetween(boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByConsigneeNameAndStatusAndTransferDateBetween(String consigneeName, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndShipperNameAndStatusAndTransferDateBetween(String item, String shipperName, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByShipperNameAndStatusAndTransferDateBetween(String shipperName, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndStatusAndTransferDateBetween(String item, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByShipperNameAndRepairServiceAndStatusAndTransferDateBetween(String shipperName, boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndRepairServiceAndStatusAndTransferDateBetween(String item, boolean repairService, String status, LocalDate startDate, LocalDate endDate);

    List<Cipl> findByItemAndTransferDate(String item, LocalDate transferDate);

    List<Cipl> findByItemAndLocationNameAndTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String created);

    List<Cipl> findByItemAndLocationNameAndStatus(String item, String locationName, String created);

    List<Cipl> findByItemAndTransferDateAndStatus(String item, LocalDate transferDate, String created);

    List<Cipl> findByLocationNameAndTransferDateAndStatus(String locationName, LocalDate transferDate, String created);

    List<Cipl> findByLocationNameAndStatus(String locationName, String created);

    List<Cipl> findByTransferDateAndStatus(LocalDate transferDate, String created);
}
