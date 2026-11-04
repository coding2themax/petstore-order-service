package com.coding2.the.max.petstore.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when horse or feeding schedule data is invalid
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidHorseDataException extends RuntimeException {
  public InvalidHorseDataException(String message) {
    super(message);
  }
}
