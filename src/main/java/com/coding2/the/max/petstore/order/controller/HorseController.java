package com.coding2.the.max.petstore.order.controller;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.coding2.the.max.petstore.order.model.FeedingSchedule;
import com.coding2.the.max.petstore.order.model.Horse;
import com.coding2.the.max.petstore.order.service.HorseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

  private final HorseService horseService;

  public HorseController(HorseService horseService) {
    this.horseService = horseService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Horse> createHorse(@RequestBody Horse horse) {
    return horseService.createHorse(horse);
  }

  @GetMapping
  public Flux<Horse> getAllHorses() {
    return horseService.getAllHorses();
  }

  @GetMapping("/{horseId}")
  public Mono<Horse> getHorse(@PathVariable Long horseId) {
    return horseService.getHorseById(horseId);
  }

  @PutMapping("/{horseId}")
  public Mono<Horse> updateHorse(@PathVariable Long horseId, @RequestBody Horse horse) {
    return horseService.updateHorse(horseId, horse);
  }

  @DeleteMapping("/{horseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteHorse(@PathVariable Long horseId) {
    return horseService.deleteHorse(horseId);
  }

  @PostMapping("/{horseId}/feedings")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<FeedingSchedule> addFeeding(@PathVariable Long horseId, @RequestBody FeedingSchedule schedule) {
    return horseService.addFeedingSchedule(horseId, schedule);
  }

  @GetMapping("/{horseId}/feedings")
  public Flux<FeedingSchedule> getFeedings(@PathVariable Long horseId) {
    return horseService.getFeedingSchedules(horseId);
  }

  @PutMapping("/{horseId}/feedings/{scheduleId}")
  public Mono<FeedingSchedule> updateFeeding(@PathVariable Long horseId, @PathVariable Long scheduleId,
      @RequestBody FeedingSchedule schedule) {
    return horseService.updateFeedingSchedule(horseId, scheduleId, schedule);
  }

  @DeleteMapping("/{horseId}/feedings/{scheduleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFeeding(@PathVariable Long horseId, @PathVariable Long scheduleId) {
    return horseService.deleteFeedingSchedule(horseId, scheduleId);
  }

  /** All horses' feedings due between two times of day, e.g. ?from=06:00&to=09:00 */
  @GetMapping("/feedings")
  public Flux<FeedingSchedule> getFeedingsBetween(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime to) {
    return horseService.getFeedingsBetween(from, to);
  }
}
