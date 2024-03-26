package com.inventory.project.repository;

import com.inventory.project.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    boolean existsByCurrencyName(String currencyName);

    @Query("SELECT c FROM Currency c WHERE c.id != :id AND c.currencyName = :currencyName")
    Currency alreadyExists(Long id, String currencyName);

    @Query("SELECT c FROM Currency c WHERE c.currencyName = :currencyName")
    Currency findByCurrencyName(String currencyName);
    Currency findTopByCurrencyName(String currencyName);


}
