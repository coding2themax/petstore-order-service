package com.coding2.the.max.petstore.order.repo;

import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;

/**
 * Repository interface for pet orders
 */
public interface OrderRepository extends ReactiveCrudRepository<PetOrder, String> {

  Flux<PetOrder> findByCustomerId(String customerId);

  Flux<PetOrder> findByStatus(OrderStatus status);
}
