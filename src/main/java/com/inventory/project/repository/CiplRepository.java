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
}
