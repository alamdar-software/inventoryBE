package com.inventory.project.repository;

import com.inventory.project.model.InternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InternalTransferRepo extends JpaRepository<InternalTransfer,Long> {
    List<InternalTransfer> findByItemAndLocationNameAndTransferDate(String item, String locationName, LocalDate transferDate);
}
