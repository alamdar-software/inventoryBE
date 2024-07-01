package com.inventory.project.repository;

import com.inventory.project.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface IncomingStockRepo extends JpaRepository<IncomingStock,Long> {
    @Query("SELECT COUNT(p) FROM IncomingStock p")
    Long findCount();

    List<IncomingStock> findByItemAndLocationAndDateBetweenOrderByDate(Item item, Location location, LocalDate fromDate, LocalDate toDate);

    List<IncomingStock> findByItemAndDateBetweenOrderByDate(Item item,LocalDate fromDate,LocalDate toDate);

    List<IncomingStock> findByLocationAndDateBetweenOrderByItemName(Location location, LocalDate fromDate, LocalDate toDate);

    List<IncomingStock> findByDateBetweenOrderByItem(LocalDate fromDate, LocalDate toDate);

    Page<IncomingStock> findByDate(LocalDate date, Pageable pageable);


    long countByDate(LocalDate todayDate);

    List<IncomingStock> findByInventoryOrderByDate(Inventory source);

    List<IncomingStock> findByStatus(String string);
    @Query("SELECT s.quantity AS quantity, s.unitCost AS unitCost, s.impaCode AS impaCode, s.remarks AS remarks, s.storeNo AS storeNo, s.sn AS sn, s.pn AS pn, s.purchaseOrder AS purchaseOrder, " +
            "s.standardPrice AS standardPrice, s.price AS price, s.extendedValue AS extendedValue, s.date AS date, i.itemName AS itemName, l.locationName AS locationName, u.unitName AS unitName, " +
            "s.currency.currencyName AS currencyName, b.brandName AS brandName, e.entityName AS entityName " +
            "FROM IncomingStock s " +
            "JOIN s.item i " +
            "JOIN s.location l " +
            "JOIN s.unit u " +
            "JOIN s.currency c " +
            "JOIN s.brand b " +
            "JOIN s.entity e " +
            "WHERE s.id = :id")
    Map<String, Object> findIncomingStockDetailsWithAssociatedFieldsById(@Param("id") Long id);

    @Query("SELECT s FROM IncomingStock s JOIN FETCH s.location WHERE s.id = :id")
    IncomingStock findIncomingStockDetailsById(@Param("id") Long id);


    List<IncomingStock> findByEntity_EntityName(String entityName);

    List<IncomingStock> findByEntity_EntityNameAndDateBetween(String entityName, LocalDate startDate, LocalDate endDate);

    List<IncomingStock> findByDateBetween(LocalDate transferDate, LocalDate plusDays);

    List<IncomingStock> findByLocation_LocationNameAndItem_Description(String locationName, String description);

    List<IncomingStock> findByLocation_LocationName(String locationName);

    List<IncomingStock> findByItem_Description(String description);

    List<IncomingStock> findByItemDescription(String itemDescription);

    List<IncomingStock> findByLocation_LocationNameAndEntity_EntityNameAndItem_DescriptionAndDateBetween(
            String locationName, String entityName, String description, LocalDate startDate, LocalDate endDate);

    List<IncomingStock> findByLocation_LocationNameAndEntity_EntityNameAndDateBetween(
            String locationName, String entityName, LocalDate startDate, LocalDate endDate);


    List<IncomingStock> findByLocation_LocationNameAndDateBetween(String locationName, LocalDate startDate, LocalDate endDate);


    List<IncomingStock> findByStatusIgnoreCase(String created);

    Integer countByQuantityGreaterThan(int quantity);


    @Query("SELECT COALESCE(SUM(quantity), 0) FROM IncomingStock WHERE location.locationName = :locationName")
    int sumQuantityByLocationName(String locationName);
    Optional<IncomingStock> findByItemId(Long itemId);

    List<IncomingStock> findByItem(Item item);




    List<IncomingStock> findByItemAndAddress(Item item, Address address);




    List<IncomingStock> findByDate(LocalDate date);

    List<IncomingStock> findByPurchaseOrder(String purchaseOrder);

    // Adjusted query method for entity name

    // Adjusted query method for item description and location name
    List<IncomingStock> findByItemDescriptionAndLocation_LocationName(String itemDescription, String locationName);

    // Adjusted query method for item description and date
    List<IncomingStock> findByItemDescriptionAndDate(String itemDescription, LocalDate date);

    // Adjusted query method for location name and date
    List<IncomingStock> findByLocation_LocationNameAndDate(String locationName, LocalDate date);

    // Adjusted query method for item description, location name, date, entity name, and purchase order
    List<IncomingStock> findByItemDescriptionAndLocation_LocationNameAndDateAndEntity_EntityNameAndPurchaseOrder(
            String itemDescription, String locationName, LocalDate date, String entityName, String purchaseOrder);

    List<IncomingStock> findByEntity_EntityNameAndStatus(String entityName, String status);

    List<IncomingStock> findByPurchaseOrderAndStatus(String purchaseOrder, String status);

    List<IncomingStock> findByDateAndStatus(LocalDate date, String status);

    List<IncomingStock> findByLocation_LocationNameAndStatus(String locationName, String status);

    List<IncomingStock> findByItemDescriptionAndStatus(String description, String status);

    List<IncomingStock> findByLocation_LocationNameAndDateAndStatus(String locationName, LocalDate date, String status);

    List<IncomingStock> findByItemDescriptionAndDateAndStatus(String description, LocalDate date, String status);

    List<IncomingStock> findByItemDescriptionAndLocation_LocationNameAndStatus(String description, String locationName, String status);

    List<IncomingStock> findByItemDescriptionAndLocation_LocationNameAndDateAndEntity_EntityNameAndPurchaseOrderAndStatus(String description, String locationName, LocalDate date, String entityName, String purchaseOrder, String status);
}
