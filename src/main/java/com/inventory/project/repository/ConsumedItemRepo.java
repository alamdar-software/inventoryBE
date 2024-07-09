package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ConsumedItemRepo extends JpaRepository<ConsumedItem,Long> {
    List<ConsumedItem> findByItemAndLocationNameAndTransferDate(String item, String locationName, LocalDate transferDate);

    List<ConsumedItem> findByItemAndLocationName(String item, String locationName);

    List<ConsumedItem> findByItem(String item);

    List<ConsumedItem> findByLocationName(String locationName);

    List<ConsumedItem> findByTransferDate(LocalDate transferDate);

    List<ConsumedItem> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);

    List<ConsumedItem> findByTransferDateBetween(LocalDate startDate, LocalDate endDate);


    List<ConsumedItem> findByItemAndTransferDateBetween(String item, LocalDate startDate, LocalDate endDate);

    List<ConsumedItem> findByLocationNameAndTransferDateBetween(String locationName, LocalDate startDate, LocalDate endDate);

    List<ConsumedItem> findByItemAndLocationNameAndTransferDateBetween(String item, String locationName, LocalDate startDate, LocalDate endDate);

    boolean existsByItemAndLocationName(String item, String locationName);

    List<ConsumedItem> findByStatus(String created);
    @Query("SELECT SUM(c.quantity) FROM ConsumedItem c WHERE c.locationName = :locationName")
    int sumQuantityByLocationName(String locationName);


    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM ConsumedItem ci WHERE ci.locationName = :locationName AND ci.items = :item")
    int sumQuantityByLocationNameAndItem(String locationName, Item item);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM ConsumedItem ci WHERE ci.items.id = :itemId")
    int sumQuantityByItemId(Long itemId);

    List<ConsumedItem> findByLocationNameAndTransferDateAndStatus(String locationName, LocalDate transferDate, String status);

    List<ConsumedItem> findByLocationNameAndStatus(String locationName, String status);

    List<ConsumedItem> findByTransferDateAndStatus(LocalDate transferDate, String status);

    List<ConsumedItem> findByItemAndStatus(String item, String status);

    List<ConsumedItem> findByItemAndLocationNameAndStatus(String item, String locationName, String status);

    List<ConsumedItem> findByItemAndLocationNameAndTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String status);

    List<ConsumedItem> findByItemAndTransferDate(String item, LocalDate transferDate);

    List<ConsumedItem> findByItemAndTransferDateAndStatus(String item, LocalDate transferDate, String status);

    List<ConsumedItem> findByStatusAndTransferDateBetween(String status, LocalDate startDate, LocalDate endDate);

    List<ConsumedItem> findByLocationNameAndStatusAndTransferDateBetween(String locationName, String status, LocalDate startDate, LocalDate endDate);

    List<ConsumedItem> findByItemAndStatusAndTransferDateBetween(String item, String status, LocalDate startDate, LocalDate endDate);

    List<ConsumedItem> findByItemAndLocationNameAndStatusAndTransferDateBetween(String item, String locationName, String status, LocalDate startDate, LocalDate endDate);
}
