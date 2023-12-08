package com.inventory.project.repository;

import com.inventory.project.model.Cipl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiplRepository extends JpaRepository<Cipl,Long> {

}
