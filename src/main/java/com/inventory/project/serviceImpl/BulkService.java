package com.inventory.project.serviceImpl;

import com.inventory.project.model.BulkStock;
import com.inventory.project.model.Cipl;
import com.inventory.project.model.IncomingStockRequest;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BulkService {
    private final BulkStockRepo bulkStockRepo;

    @Autowired
    public BulkService(BulkStockRepo bulkStockRepo) {
        this.bulkStockRepo = bulkStockRepo;
    }

    public List<BulkStock> getAllBulk() {
        return bulkStockRepo.findAll();
    }

    public Optional<BulkStock> getBulkById(Long id) {
        return bulkStockRepo.findById(id);
    }
    @Transactional
    public BulkStock createBulk(BulkStock bulkStock) {
        BulkStock savedBulkStock = bulkStockRepo.save(bulkStock);
        bulkStockRepo.flush(); // Explicitly flushing to the database
        return savedBulkStock;
    }

    public void deleteBulkById(Long id) {
        bulkStockRepo.deleteById(id);
    }


}
