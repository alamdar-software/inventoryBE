package com.inventory.project.repository;

import com.inventory.project.model.Mto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MtoRepository extends JpaRepository<Mto,Long> {
}
