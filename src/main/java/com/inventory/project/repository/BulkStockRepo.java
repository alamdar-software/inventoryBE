package com.inventory.project.repository;

import com.inventory.project.model.BulkStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface BulkStockRepo extends JpaRepository<BulkStock,Long> {
    @Query("SELECT s.quantity AS quantity, s.unitPrice AS unitPrice, s.impaCode AS impaCode, s.remarks AS remarks, s.storeNo AS storeNo, s.sn AS sn, s.pn AS pn, s.purchaseOrder AS purchaseOrder, " +
            "s.standardPrice AS standardPrice, s.price AS price, s.extendedValue AS extendedValue, s.date AS date, i.itemName AS itemName, l.locationName AS locationName, u.unitName AS unitName, " +
            "s.currency.currencyName AS currencyName, b.brandName AS brandName, e.entityName AS entityName " +
            "FROM BulkStock s " +
            "JOIN s.item i " +
            "JOIN s.location l " +
            "JOIN s.unit u " +
            "JOIN s.currency c " +
            "JOIN s.brand b " +
            "JOIN s.entity e " +
            "WHERE s.id = :id")
    Map<String, Object> findBulkStockDetailsWithAssociatedFieldsById(@Param("id") Long id);
}
