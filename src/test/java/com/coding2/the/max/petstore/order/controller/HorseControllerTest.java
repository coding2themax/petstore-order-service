package com.coding2.the.max.petstore.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2.the.max.petstore.order.exception.HorseNotFoundException;
import com.coding2.the.max.petstore.order.exception.InvalidHorseDataException;
import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;
import com.coding2.the.max.petstore.order.service.HorseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(HorseController.class)
class HorseControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private HorseService horseService;

  private static Horse horse() {
    Horse horse = new Horse();
    horse.setHorseId(1L);
    horse.setName("Spirit");
    horse.setBreed("Mustang");
    return horse;
  }

  private static FeedingSchedule feeding() {
    FeedingSchedule schedule = new FeedingSchedule();
    schedule.setScheduleId(10L);
    schedule.setHorseId(1L);
    schedule.setFeedTime(LocalTime.of(7, 0));
    schedule.setFeedType("Hay");
    schedule.setQuantityKg(2.5);
    return schedule;
  }

  @Test
  void createHorse() {
    when(horseService.createHorse(any(Horse.class))).thenReturn(Mono.just(horse()));

    webTestClient.post().uri("/api/horses")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(horse())
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Horse.class).isEqualTo(horse());
  }

  @Test
  void createHorseInvalidReturns400() {
    when(horseService.createHorse(any(Horse.class)))
        .thenReturn(Mono.error(new InvalidHorseDataException("Horse name is required")));

    webTestClient.post().uri("/api/horses")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new Horse())
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void getAllHorses() {
    when(horseService.getAllHorses()).thenReturn(Flux.just(horse()));

    webTestClient.get().uri("/api/horses")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Horse.class).hasSize(1).contains(horse());
  }

  @Test
  void getHorse() {
    when(horseService.getHorseById(1L)).thenReturn(Mono.just(horse()));

    webTestClient.get().uri("/api/horses/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Horse.class).isEqualTo(horse());
  }

  @Test
  void getMissingHorseReturns404() {
    when(horseService.getHorseById(2L)).thenReturn(Mono.error(new HorseNotFoundException("missing")));

    webTestClient.get().uri("/api/horses/2")
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void updateHorse() {
    when(horseService.updateHorse(eq(1L), any(Horse.class))).thenReturn(Mono.just(horse()));

    webTestClient.put().uri("/api/horses/1")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(horse())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Horse.class).isEqualTo(horse());
  }

  @Test
  void deleteHorse() {
    when(horseService.deleteHorse(1L)).thenReturn(Mono.empty());

    webTestClient.delete().uri("/api/horses/1")
        .exchange()
        .expectStatus().isNoContent();
    verify(horseService).deleteHorse(1L);
  }

  @Test
  void addFeeding() {
    when(horseService.addFeedingSchedule(eq(1L), any(FeedingSchedule.class))).thenReturn(Mono.just(feeding()));

    webTestClient.post().uri("/api/horses/1/feedings")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(feeding())
        .exchange()
        .expectStatus().isCreated()
        .expectBody(FeedingSchedule.class).isEqualTo(feeding());
  }

  @Test
  void getFeedings() {
    when(horseService.getFeedingSchedules(1L)).thenReturn(Flux.just(feeding()));

    webTestClient.get().uri("/api/horses/1/feedings")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(FeedingSchedule.class).hasSize(1).contains(feeding());
  }

  @Test
  void updateFeeding() {
    when(horseService.updateFeedingSchedule(eq(1L), eq(10L), any(FeedingSchedule.class)))
        .thenReturn(Mono.just(feeding()));

    webTestClient.put().uri("/api/horses/1/feedings/10")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(feeding())
        .exchange()
        .expectStatus().isOk()
        .expectBody(FeedingSchedule.class).isEqualTo(feeding());
  }

  @Test
  void deleteFeeding() {
    when(horseService.deleteFeedingSchedule(1L, 10L)).thenReturn(Mono.empty());

    webTestClient.delete().uri("/api/horses/1/feedings/10")
        .exchange()
        .expectStatus().isNoContent();
    verify(horseService).deleteFeedingSchedule(1L, 10L);
  }

  @Test
  void getFeedingsBetween() {
    when(horseService.getFeedingsBetween(LocalTime.of(6, 0), LocalTime.of(9, 0)))
        .thenReturn(Flux.just(feeding()));

    webTestClient.get().uri("/api/horses/feedings?from=06:00&to=09:00")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(FeedingSchedule.class).hasSize(1).contains(feeding());
  }
}
