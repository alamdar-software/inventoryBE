package com.inventory.project.repository;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.ScrappedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScrappedItemRepository extends JpaRepository<ScrappedItem,Long> {
    List<ScrappedItem> findByItemAndLocationNameAndTransferDate(String item, String locationName, LocalDate transferDate);

    List<ScrappedItem> findByItemAndLocationName(String item, String locationName);

    List<ScrappedItem> findByItem(String item);

    List<ScrappedItem> findByLocationName(String locationName);

    List<ScrappedItem> findByTransferDate(LocalDate transferDate);

    List<ScrappedItem> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);

    List<ScrappedItem> findByLocationNameAndTransferDateBetween(String locationName, LocalDate startDate, LocalDate endDate);

    List<ScrappedItem> findByItemAndTransferDateBetween(String item, LocalDate startDate, LocalDate endDate);

    List<ScrappedItem> findByTransferDateBetween(LocalDate startDate, LocalDate endDate);
}
