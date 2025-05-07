package com.coding2.the.max.petstore.order.repo;

import com.coding2.the.max.petstore.order.model.PetOrder;

/**
 * Interface for inventory management service
 */
public interface InventoryService {
  boolean isAvailable(String petId, int quantity);

  void reserveInventory(PetOrder order);

  void returnInventoryForOrder(PetOrder order);
}