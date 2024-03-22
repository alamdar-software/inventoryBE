package com.inventory.project.repository;

import com.inventory.project.model.BulkStockDto;
import com.inventory.project.model.PRTItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PRTItemDetailRepo extends JpaRepository<PRTItemDetail,Long> {

    List<PRTItemDetail> findByInventory_Id(Long id);

//    @Query("Select New  com.inventory.demo.model.TransferDTO(i.id, i.purchaseItem,COALESCE(i.purchasedQty, i.receivedQty),i.remainingQty) from PRTItemDetail i where i.inventory.id=:id AND i.remainingQty >0 ORDER BY i.purchaseItem.date ASC")
//    List<TransferDTO> findByInventoryId(Long id);

    @Query("Select New  com.inventory.project.model.BulkStockDto(i.incomingStock.purchaseOrder,i.purchaseDate,i.purchasedQty,i.remainingQty,i.transferredQty) from PRTItemDetail i where i.inventory.id=:id AND i.type=:type")
    List<BulkStockDto> findByInventory(Long id, String type);

    @Query("SELECT NEW com.inventory.project.model.BulkStockDto(i.incomingStock.purchaseOrder, i.receivedDate, i.receivedQty, i.remainingQty, i.transferredQty) FROM PRTItemDetail i WHERE i.inventory.id = :id AND i.type = :type")
    List<BulkStockDto> findByInventoryAndType(@Param("id") Long id, @Param("type") String type);

    @Modifying
    @Transactional
    @Query("UPDATE PRTItemDetail pd SET pd.purchasedQty = pd.purchasedQty + ?1")
    void updatePurchasedQty(int quantity);

}
