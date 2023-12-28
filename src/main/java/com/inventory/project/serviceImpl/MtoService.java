package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.Mto;
import com.inventory.project.repository.MtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MtoService {

    private final MtoRepository mtoRepository;

    @Autowired
    public MtoService(MtoRepository mtoRepository) {
        this.mtoRepository = mtoRepository;
    }

    public List<Mto> getAllMto() {
        return mtoRepository.findAll();
    }

    public Optional<Mto> getMtoById(Long id) {
        return mtoRepository.findById(id);
    }

    public Mto createMto(Mto mto) {
        return mtoRepository.save(mto);
    }

    public void deleteMtoById(Long id) {
        mtoRepository.deleteById(id);
    }


    public List<Mto> getMtoByItemAndLocationAndTransferDate(List<String> item, String locationName, LocalDate transferDate) {
        return mtoRepository.findByItemInAndLocationNameAndTransferDate(item, locationName, transferDate);
    }

}
