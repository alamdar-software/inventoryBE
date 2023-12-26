package com.inventory.project.repository;

import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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


}
