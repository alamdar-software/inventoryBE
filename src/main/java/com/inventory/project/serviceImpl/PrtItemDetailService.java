package com.inventory.project.serviceImpl;

import com.inventory.project.model.PRTItemDetail;
import com.inventory.project.repository.PRTItemDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrtItemDetailService {
    private PRTItemDetailRepo prtItemDetailRepository;
    private  PrtItemDetailService prtItemDetailService;

    @Autowired
    public void setPrtItemDetailRepository(PRTItemDetailRepo prtItemDetailRepository) {
        this.prtItemDetailRepository = prtItemDetailRepository;
    }

    public List<PRTItemDetail> getAllPRTItemDetails() {
        return prtItemDetailRepository.findAll();
    }

    public PRTItemDetail getPRTItemDetailById(Long id) {
        return prtItemDetailRepository.findById(id).orElse(null);
    }
}
