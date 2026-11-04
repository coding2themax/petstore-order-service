package com.coding2.the.max.petstore.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a horse cannot be found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class HorseNotFoundException extends RuntimeException {
  public HorseNotFoundException(String message) {
    super(message);
  }
}
