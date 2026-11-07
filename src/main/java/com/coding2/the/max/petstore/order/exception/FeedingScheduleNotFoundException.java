package com.coding2.the.max.petstore.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a feeding schedule cannot be found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FeedingScheduleNotFoundException extends RuntimeException {
  public FeedingScheduleNotFoundException(String message) {
    super(message);
  }
}
