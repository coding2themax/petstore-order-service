package com.coding2.the.max.petstore.order.repo;

import java.time.LocalTime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.coding2.the.max.petstore.order.model.FeedingSchedule;

import reactor.core.publisher.Flux;

/**
 * Repository for horse feeding schedules
 */
public interface FeedingScheduleRepository extends ReactiveCrudRepository<FeedingSchedule, Long> {

  Flux<FeedingSchedule> findByHorseIdOrderByFeedTimeAsc(Long horseId);

  Flux<FeedingSchedule> findByFeedTimeBetweenOrderByFeedTimeAsc(LocalTime from, LocalTime to);
}
