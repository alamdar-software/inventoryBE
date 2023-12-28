package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface MtoRepository extends JpaRepository<Mto,Long> {
    List<Mto> findByItemInAndLocationNameAndTransferDate(List<String> item, String locationName, LocalDate transferDate);

}
