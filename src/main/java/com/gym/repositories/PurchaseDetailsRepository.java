package com.gym.repositories;

import com.gym.entities.PurchaseDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDetailsRepository extends CrudRepository<PurchaseDetails, Long> {

    // Método para obtener todos los detalles de la compra para una compra específica
    List<PurchaseDetails> findByPurchaseId(Long purchaseId);

    // Método para obtener todos los detalles de la compra para un producto específico
    List<PurchaseDetails> findByProductId(Long productId);

}
