package com.coding2.the.max.petstore.order.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.coding2.the.max.petstore.order.model.Horse;

/**
 * Repository for horses
 */
public interface HorseRepository extends ReactiveCrudRepository<Horse, Long> {
}
