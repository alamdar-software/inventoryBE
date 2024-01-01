package com.inventory.project.repository;

import com.inventory.project.model.InternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalTransferRepo extends JpaRepository<InternalTransfer,Long> {
}
