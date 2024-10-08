package com.inventory.project.repository;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT it FROM InternalTransfer it JOIN it.description desc WHERE desc LIKE %:description%")
    List<InternalTransfer> findITByDescriptionContaining(@Param("description") String description);

    List<InternalTransfer> findITByDescriptionAndLocationNameAndTransferDateAndStatus(String description, String locationName, LocalDate transferDate, String status);

    List<InternalTransfer> findITByReferenceNo(String referenceNumber);

    List<InternalTransfer> findITByStatus(String status);

    List<InternalTransfer> findITByLocationNameAndStatus(String locationName, String status);

    List<InternalTransfer> findITByDescriptionAndLocationNameAndStatus(String description, String locationName, String status);

    @Query("SELECT it FROM InternalTransfer it JOIN it.description desc WHERE desc LIKE %:description% AND it.status = :status")
    List<InternalTransfer> findByDescriptionContainingAndStatus(@Param("description") String description, @Param("status") String status);

    List<InternalTransfer> findByDescriptionAndLocationNameAndStatus(String description, String locationName, String status);


//    

   

    List<InternalTransfer> findByLocationNameAndStatus(String locationName, String status);

    List<InternalTransfer> findByDescriptionContaining(String description);


    List<InternalTransfer> findByDescriptionAndLocationNameAndTransferDateAndStatus(
            String description, String locationName, LocalDate transferDate, String status);


    List<InternalTransfer> findByDescriptionAndTransferDateAndStatus(
            String description, LocalDate transferDate, String status);

    List<InternalTransfer> findByLocationNameAndTransferDateAndStatus(
            String locationName, LocalDate transferDate, String status);

    List<InternalTransfer> findByDescriptionAndStatus(String description, String status);


    List<InternalTransfer> findByTransferDateAndStatus(LocalDate transferDate, String status);

}
