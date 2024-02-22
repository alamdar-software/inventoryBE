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

    List<InternalTransfer> findByDescriptionAndTransferDateBetween(String description, LocalDate startDate, LocalDate endDate);

    List<InternalTransfer> findByLocationNameAndTransferDateBetween(String locationName, LocalDate startDate, LocalDate endDate);

    List<InternalTransfer> findByDescriptionAndLocationNameAndTransferDateBetween(String description, String locationName, LocalDate startDate, LocalDate endDate);

    List<InternalTransfer> findByTransferDateBetweenAndLocationName(LocalDate startDate, LocalDate endDate, String locationName);

    List<InternalTransfer> findByTransferDateBetweenAndDescription(LocalDate startDate, LocalDate endDate, String description);

    List<InternalTransfer> findByLocationNameAndTransferDateBetweenAndDescription(String locationName, LocalDate startDate, LocalDate endDate, String description);

    List<InternalTransfer> findByLocationNameAndDescription(String locationName, String description);

    List<InternalTransfer> findByStatus(String created);
}
