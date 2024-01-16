package com.inventory.project.repository;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InternalTransferRepo extends JpaRepository<InternalTransfer,Long> {
    List<InternalTransfer> findByDescriptionAndLocationNameAndTransferDate(String description, String locationName, LocalDate transferDate);


    List<InternalTransfer> findByDescriptionAndLocationName(String description, String locationName);

    List<InternalTransfer> findByDescription(String description);

    List<InternalTransfer> findByLocationNameAndTransferDate(String locationName, LocalDate transferDate);

    List<InternalTransfer> findByLocationName(String locationName);

    List<InternalTransfer> findByTransferDate(LocalDate transferDate);

    List<InternalTransfer> findByTransferDateBetween(LocalDate startDate, LocalDate endDate);

}
