package com.coding2.the.max.petstore.order.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coding2.the.max.petstore.order.exception.FeedingScheduleNotFoundException;
import com.coding2.the.max.petstore.order.exception.HorseNotFoundException;
import com.coding2.the.max.petstore.order.exception.InvalidHorseDataException;
import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;
import com.coding2.the.max.petstore.order.repo.FeedingScheduleRepository;
import com.coding2.the.max.petstore.order.repo.HorseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HorseServiceImplTest {

  @Mock
  private HorseRepository horseRepository;

  @Mock
  private FeedingScheduleRepository feedingScheduleRepository;

  private HorseServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new HorseServiceImpl(horseRepository, feedingScheduleRepository);
  }

  private static Horse horse(Long id, String name) {
    Horse horse = new Horse();
    horse.setHorseId(id);
    horse.setName(name);
    return horse;
  }

  private static FeedingSchedule schedule(Long id, Long horseId, String time, String type, Double qty) {
    FeedingSchedule schedule = new FeedingSchedule();
    schedule.setScheduleId(id);
    schedule.setHorseId(horseId);
    schedule.setFeedTime(time == null ? null : LocalTime.parse(time));
    schedule.setFeedType(type);
    schedule.setQuantityKg(qty);
    return schedule;
  }

  @Test
  void createHorseClearsIdAndSaves() {
    Horse input = horse(99L, "Spirit");
    when(horseRepository.save(any(Horse.class))).thenAnswer(inv -> {
      Horse saved = inv.getArgument(0);
      saved.setHorseId(1L);
      return Mono.just(saved);
    });

    StepVerifier.create(service.createHorse(input))
        .expectNextMatches(h -> h.getHorseId() == 1L && h.getName().equals("Spirit"))
        .verifyComplete();
  }

  @Test
  void createHorseRejectsMissingName() {
    StepVerifier.create(service.createHorse(horse(null, " ")))
        .expectError(InvalidHorseDataException.class)
        .verify();
    StepVerifier.create(service.createHorse(null))
        .expectError(InvalidHorseDataException.class)
        .verify();
    verify(horseRepository, never()).save(any());
  }

  @Test
  void createHorseRejectsNonPositiveWeight() {
    Horse input = horse(null, "Spirit");
    input.setWeightKg(0.0);
    StepVerifier.create(service.createHorse(input))
        .expectError(InvalidHorseDataException.class)
        .verify();
  }

  @Test
  void getHorseByIdNotFound() {
    when(horseRepository.findById(5L)).thenReturn(Mono.empty());
    StepVerifier.create(service.getHorseById(5L))
        .expectError(HorseNotFoundException.class)
        .verify();
  }

  @Test
  void getAllHorses() {
    when(horseRepository.findAll()).thenReturn(Flux.just(horse(1L, "A"), horse(2L, "B")));
    StepVerifier.create(service.getAllHorses()).expectNextCount(2).verifyComplete();
  }

  @Test
  void updateHorseKeepsPathId() {
    Horse input = horse(42L, "Renamed");
    input.setWeightKg(500.0);
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "Old")));
    when(horseRepository.save(any(Horse.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    StepVerifier.create(service.updateHorse(1L, input))
        .expectNextMatches(h -> h.getHorseId() == 1L && h.getName().equals("Renamed"))
        .verifyComplete();
  }

  @Test
  void deleteHorse() {
    Horse existing = horse(1L, "A");
    when(horseRepository.findById(1L)).thenReturn(Mono.just(existing));
    when(horseRepository.delete(existing)).thenReturn(Mono.empty());

    StepVerifier.create(service.deleteHorse(1L)).verifyComplete();
    verify(horseRepository).delete(existing);
  }

  @Test
  void addFeedingScheduleAttachesToHorse() {
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.save(any(FeedingSchedule.class)))
        .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    StepVerifier.create(service.addFeedingSchedule(1L, schedule(7L, 3L, "07:00", "Hay", 2.5)))
        .expectNextMatches(s -> s.getScheduleId() == null && s.getHorseId() == 1L)
        .verifyComplete();
  }

  @Test
  void addFeedingScheduleForMissingHorse() {
    when(horseRepository.findById(1L)).thenReturn(Mono.empty());
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, "07:00", "Hay", 2.5)))
        .expectError(HorseNotFoundException.class)
        .verify();
  }

  @Test
  void addFeedingScheduleValidation() {
    StepVerifier.create(service.addFeedingSchedule(1L, null))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, null, "Hay", 2.5)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, "07:00", "", 2.5)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, "07:00", null, 2.5)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, "07:00", "Hay", null)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.addFeedingSchedule(1L, schedule(null, null, "07:00", "Hay", -1.0)))
        .expectError(InvalidHorseDataException.class).verify();
  }

  @Test
  void getFeedingSchedules() {
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.findByHorseIdOrderByFeedTimeAsc(1L))
        .thenReturn(Flux.just(schedule(1L, 1L, "07:00", "Hay", 2.0), schedule(2L, 1L, "18:00", "Oats", 1.0)));

    StepVerifier.create(service.getFeedingSchedules(1L)).expectNextCount(2).verifyComplete();
  }

  @Test
  void updateFeedingSchedule() {
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.findById(5L)).thenReturn(Mono.just(schedule(5L, 1L, "07:00", "Hay", 2.0)));
    when(feedingScheduleRepository.save(any(FeedingSchedule.class)))
        .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

    StepVerifier.create(service.updateFeedingSchedule(1L, 5L, schedule(null, null, "08:00", "Oats", 1.5)))
        .expectNextMatches(s -> s.getScheduleId() == 5L && s.getHorseId() == 1L
            && s.getFeedTime().equals(LocalTime.of(8, 0)))
        .verifyComplete();
  }

  @Test
  void updateFeedingScheduleOwnedByAnotherHorse() {
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.findById(5L)).thenReturn(Mono.just(schedule(5L, 2L, "07:00", "Hay", 2.0)));

    StepVerifier.create(service.updateFeedingSchedule(1L, 5L, schedule(null, null, "08:00", "Oats", 1.5)))
        .expectError(FeedingScheduleNotFoundException.class)
        .verify();
  }

  @Test
  void deleteFeedingSchedule() {
    FeedingSchedule existing = schedule(5L, 1L, "07:00", "Hay", 2.0);
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.findById(5L)).thenReturn(Mono.just(existing));
    when(feedingScheduleRepository.delete(existing)).thenReturn(Mono.empty());

    StepVerifier.create(service.deleteFeedingSchedule(1L, 5L)).verifyComplete();
    verify(feedingScheduleRepository).delete(existing);
  }

  @Test
  void deleteFeedingScheduleNotFound() {
    when(horseRepository.findById(1L)).thenReturn(Mono.just(horse(1L, "A")));
    when(feedingScheduleRepository.findById(5L)).thenReturn(Mono.empty());

    StepVerifier.create(service.deleteFeedingSchedule(1L, 5L))
        .expectError(FeedingScheduleNotFoundException.class)
        .verify();
  }

  @Test
  void getFeedingsBetween() {
    LocalTime from = LocalTime.of(6, 0);
    LocalTime to = LocalTime.of(9, 0);
    when(feedingScheduleRepository.findByFeedTimeBetweenOrderByFeedTimeAsc(from, to))
        .thenReturn(Flux.just(schedule(1L, 1L, "07:00", "Hay", 2.0)));

    StepVerifier.create(service.getFeedingsBetween(from, to)).expectNextCount(1).verifyComplete();
  }

  @Test
  void getFeedingsBetweenRejectsBadRange() {
    StepVerifier.create(service.getFeedingsBetween(LocalTime.of(9, 0), LocalTime.of(6, 0)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.getFeedingsBetween(null, LocalTime.of(6, 0)))
        .expectError(InvalidHorseDataException.class).verify();
    StepVerifier.create(service.getFeedingsBetween(LocalTime.of(6, 0), null))
        .expectError(InvalidHorseDataException.class).verify();
  }
}
