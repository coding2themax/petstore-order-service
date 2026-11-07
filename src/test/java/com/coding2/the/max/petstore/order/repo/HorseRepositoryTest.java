package com.coding2.the.max.petstore.order.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Testcontainers(disabledWithoutDocker = true)
class HorseRepositoryTest {

  @Autowired
  private HorseRepository horseRepository;

  @Autowired
  private FeedingScheduleRepository feedingScheduleRepository;

  @BeforeEach
  void setUp() {
    feedingScheduleRepository.deleteAll().then(horseRepository.deleteAll()).block();
  }

  private static FeedingSchedule feeding(Long horseId, LocalTime time, String type) {
    FeedingSchedule schedule = new FeedingSchedule();
    schedule.setHorseId(horseId);
    schedule.setFeedTime(time);
    schedule.setFeedType(type);
    schedule.setQuantityKg(2.0);
    return schedule;
  }

  @Test
  void savesHorseWithGeneratedIdAndOrderedFeedings() {
    Horse horse = new Horse();
    horse.setName("Spirit");
    horse.setWeightKg(450.5);

    Flux<FeedingSchedule> result = horseRepository.save(horse)
        .flatMapMany(saved -> {
          assertThat(saved.getHorseId()).isNotNull();
          return feedingScheduleRepository.save(feeding(saved.getHorseId(), LocalTime.of(18, 0), "Oats"))
              .then(feedingScheduleRepository.save(feeding(saved.getHorseId(), LocalTime.of(7, 0), "Hay")))
              .thenMany(feedingScheduleRepository.findByHorseIdOrderByFeedTimeAsc(saved.getHorseId()));
        });

    StepVerifier.create(result)
        .expectNextMatches(s -> s.getFeedType().equals("Hay"))
        .expectNextMatches(s -> s.getFeedType().equals("Oats"))
        .verifyComplete();
  }

  @Test
  void findsFeedingsInTimeWindow() {
    Horse horse = new Horse();
    horse.setName("Bella");

    Flux<FeedingSchedule> result = horseRepository.save(horse)
        .flatMapMany(saved -> feedingScheduleRepository.save(feeding(saved.getHorseId(), LocalTime.of(7, 0), "Hay"))
            .then(feedingScheduleRepository.save(feeding(saved.getHorseId(), LocalTime.of(18, 0), "Oats")))
            .thenMany(feedingScheduleRepository.findByFeedTimeBetweenOrderByFeedTimeAsc(
                LocalTime.of(6, 0), LocalTime.of(9, 0))));

    StepVerifier.create(result)
        .expectNextMatches(s -> s.getFeedTime().equals(LocalTime.of(7, 0)))
        .verifyComplete();
  }
}
