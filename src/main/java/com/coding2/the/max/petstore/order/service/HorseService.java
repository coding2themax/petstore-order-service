package com.coding2.the.max.petstore.order.service;

import java.time.LocalTime;

import com.coding2.the.max.petstore.order.exception.FeedingScheduleNotFoundException;
import com.coding2.the.max.petstore.order.exception.HorseNotFoundException;
import com.coding2.the.max.petstore.order.exception.InvalidHorseDataException;
import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Operations for tracking horses and their feeding schedules.
 * Errors are signalled with {@link HorseNotFoundException},
 * {@link FeedingScheduleNotFoundException} and
 * {@link InvalidHorseDataException}.
 */
public interface HorseService {

  Mono<Horse> createHorse(Horse horse);

  Mono<Horse> getHorseById(Long horseId);

  Flux<Horse> getAllHorses();

  Mono<Horse> updateHorse(Long horseId, Horse horse);

  /** Deletes the horse; its feeding schedules are removed with it. */
  Mono<Void> deleteHorse(Long horseId);

  Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule);

  /** Feedings for one horse, earliest time of day first. */
  Flux<FeedingSchedule> getFeedingSchedules(Long horseId);

  Mono<FeedingSchedule> updateFeedingSchedule(Long horseId, Long scheduleId, FeedingSchedule schedule);

  Mono<Void> deleteFeedingSchedule(Long horseId, Long scheduleId);

  /** Feedings across all horses between two times of day (inclusive). */
  Flux<FeedingSchedule> getFeedingsBetween(LocalTime from, LocalTime to);
}
