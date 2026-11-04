package com.coding2.the.max.petstore.order.model;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

/**
 * A recurring daily feeding for a horse, e.g. 2.5 kg of hay at 07:00.
 */
@Data
@Table(name = "feeding_schedule", schema = "petstore")
public class FeedingSchedule {
  @Id
  @Column(value = "schedule_id")
  private Long scheduleId;

  @Column(value = "horse_id")
  private Long horseId;

  @Column(value = "feed_time")
  private LocalTime feedTime;

  @Column(value = "feed_type")
  private String feedType;

  @Column(value = "quantity_kg")
  private Double quantityKg;

  @Column(value = "notes")
  private String notes;
}
