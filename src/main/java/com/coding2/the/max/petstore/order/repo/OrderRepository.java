package com.coding2.the.max.petstore.order.repo;

import java.util.List;
import java.util.Optional;

import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;

/**
 * Repository interface for pet orders
 */
public interface OrderRepository {
  void save(PetOrder order);

  Optional<PetOrder> findById(String orderId);

  List<PetOrder> findByCustomerId(String customerId);

  List<PetOrder> findByStatus(OrderStatus status);
}
