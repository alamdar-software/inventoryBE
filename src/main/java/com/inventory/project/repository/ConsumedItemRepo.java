package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
}
