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
@Repository
public interface IncomingStockRepo extends JpaRepository<IncomingStock,Long> {
    @Query("SELECT COUNT(p) FROM IncomingStock p")
    Long findCount();

    List<IncomingStock> findByItemAndLocationAndDateBetweenOrderByDate(Item item, Location location, LocalDate fromDate, LocalDate toDate);

    List<IncomingStock> findByItemAndDateBetweenOrderByDate(Item item,LocalDate fromDate,LocalDate toDate);

    List<IncomingStock> findByLocationAndDateBetweenOrderByItem(Location location,LocalDate fromDate,LocalDate toDate);

    List<IncomingStock> findByDateBetweenOrderByItem(LocalDate fromDate, LocalDate toDate);

    Page<IncomingStock> findByDate(LocalDate date, Pageable pageable);


    long countByDate(LocalDate todayDate);

    List<IncomingStock> findByInventoryOrderByDate(Inventory source);

    List<IncomingStock> findByStatus(String string);
    @Query("SELECT s.quantity, s.unitCost, s.impaCode, s.remarks, s.storeNo, s.sn, s.pn, s.purchaseOrder, " +
            "s.standardPrice, s.price, s.extendedValue, s.date, i.itemName, l.locationName, u.unitName, " +
            "s.currency.currencyName, b.brandName, e.entityName " +
            "FROM IncomingStock s " +
            "JOIN s.item i " +
            "JOIN s.location l " +
            "JOIN s.unit u " +
            "JOIN s.currency c " +
            "JOIN s.brand b " +
            "JOIN s.entity e " +
            "WHERE s.id = :id")
    Object[] findIncomingStockDetailsWithAssociatedFieldsById(@Param("id") Long id);


    @Query("SELECT s FROM IncomingStock s JOIN FETCH s.location WHERE s.id = :id")
    IncomingStock findIncomingStockDetailsById(@Param("id") Long id);

}
