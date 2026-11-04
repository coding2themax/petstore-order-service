package com.coding2.the.max.petstore.order.service.impl;

import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.coding2.the.max.petstore.order.exception.FeedingScheduleNotFoundException;
import com.coding2.the.max.petstore.order.exception.HorseNotFoundException;
import com.coding2.the.max.petstore.order.exception.InvalidHorseDataException;
import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;
import com.coding2.the.max.petstore.order.repo.FeedingScheduleRepository;
import com.coding2.the.max.petstore.order.repo.HorseRepository;
import com.coding2.the.max.petstore.order.service.HorseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HorseServiceImpl implements HorseService {

  private final HorseRepository horseRepository;
  private final FeedingScheduleRepository feedingScheduleRepository;

  public HorseServiceImpl(HorseRepository horseRepository, FeedingScheduleRepository feedingScheduleRepository) {
    this.horseRepository = horseRepository;
    this.feedingScheduleRepository = feedingScheduleRepository;
  }

  @Override
  public Mono<Horse> createHorse(Horse horse) {
    return validateHorse(horse).flatMap(valid -> {
      valid.setHorseId(null);
      return horseRepository.save(valid);
    });
  }

  @Override
  public Mono<Horse> getHorseById(Long horseId) {
    return horseRepository.findById(horseId)
        .switchIfEmpty(Mono.error(() -> new HorseNotFoundException("Horse not found with ID: " + horseId)));
  }

  @Override
  public Flux<Horse> getAllHorses() {
    return horseRepository.findAll();
  }

  @Override
  public Mono<Horse> updateHorse(Long horseId, Horse horse) {
    return validateHorse(horse)
        .flatMap(valid -> getHorseById(horseId).flatMap(existing -> {
          valid.setHorseId(horseId);
          return horseRepository.save(valid);
        }));
  }

  @Override
  public Mono<Void> deleteHorse(Long horseId) {
    return getHorseById(horseId).flatMap(horseRepository::delete);
  }

  @Override
  public Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule) {
    return validateSchedule(schedule)
        .flatMap(valid -> getHorseById(horseId).flatMap(horse -> {
          valid.setScheduleId(null);
          valid.setHorseId(horseId);
          return feedingScheduleRepository.save(valid);
        }));
  }

  @Override
  public Flux<FeedingSchedule> getFeedingSchedules(Long horseId) {
    return getHorseById(horseId)
        .flatMapMany(horse -> feedingScheduleRepository.findByHorseIdOrderByFeedTimeAsc(horseId));
  }

  @Override
  public Mono<FeedingSchedule> updateFeedingSchedule(Long horseId, Long scheduleId, FeedingSchedule schedule) {
    return validateSchedule(schedule)
        .flatMap(valid -> getScheduleForHorse(horseId, scheduleId).flatMap(existing -> {
          valid.setScheduleId(scheduleId);
          valid.setHorseId(horseId);
          return feedingScheduleRepository.save(valid);
        }));
  }

  @Override
  public Mono<Void> deleteFeedingSchedule(Long horseId, Long scheduleId) {
    return getScheduleForHorse(horseId, scheduleId).flatMap(feedingScheduleRepository::delete);
  }

  @Override
  public Flux<FeedingSchedule> getFeedingsBetween(LocalTime from, LocalTime to) {
    if (from == null || to == null || from.isAfter(to)) {
      return Flux.error(new InvalidHorseDataException("'from' must be on or before 'to'"));
    }
    return feedingScheduleRepository.findByFeedTimeBetweenOrderByFeedTimeAsc(from, to);
  }

  private Mono<FeedingSchedule> getScheduleForHorse(Long horseId, Long scheduleId) {
    return getHorseById(horseId).then(feedingScheduleRepository.findById(scheduleId))
        .filter(schedule -> horseId.equals(schedule.getHorseId()))
        .switchIfEmpty(Mono.error(() -> new FeedingScheduleNotFoundException(
            "Feeding schedule " + scheduleId + " not found for horse " + horseId)));
  }

  private Mono<Horse> validateHorse(Horse horse) {
    if (horse == null || horse.getName() == null || horse.getName().isBlank()) {
      return Mono.error(new InvalidHorseDataException("Horse name is required"));
    }
    if (horse.getWeightKg() != null && horse.getWeightKg() <= 0) {
      return Mono.error(new InvalidHorseDataException("Horse weight must be positive"));
    }
    return Mono.just(horse);
  }

  private Mono<FeedingSchedule> validateSchedule(FeedingSchedule schedule) {
    if (schedule == null || schedule.getFeedTime() == null) {
      return Mono.error(new InvalidHorseDataException("Feed time is required"));
    }
    if (schedule.getFeedType() == null || schedule.getFeedType().isBlank()) {
      return Mono.error(new InvalidHorseDataException("Feed type is required"));
    }
    if (schedule.getQuantityKg() == null || schedule.getQuantityKg() <= 0) {
      return Mono.error(new InvalidHorseDataException("Feed quantity must be positive"));
    }
    return Mono.just(schedule);
  }
}
