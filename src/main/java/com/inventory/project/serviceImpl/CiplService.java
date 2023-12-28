package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.repository.CiplRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CiplService {
    private LocalDate transferDate;
    private String locationName;
    private List<String> item;
    private final CiplRepository ciplRepository;

    @Autowired
    public CiplService(CiplRepository ciplRepository) {
        this.ciplRepository = ciplRepository;
    }

    public List<Cipl> getAllCipl() {
        return ciplRepository.findAll();
    }

    public Optional<Cipl> getCiplById(Long id) {
        return ciplRepository.findById(id);
    }

    public Cipl createCipl(Cipl cipl) {
        return ciplRepository.save(cipl);
    }

    public void deleteCiplById(Long id) {
        ciplRepository.deleteById(id);
    }


    // Other methods for CRUD operations...
    public List<Cipl> getCiplByItemAndLocationAndTransferDate(List<String> item, String locationName, LocalDate transferDate) {
        return ciplRepository.findByItemInAndLocationNameAndTransferDate(item, locationName, transferDate);
    }

}
