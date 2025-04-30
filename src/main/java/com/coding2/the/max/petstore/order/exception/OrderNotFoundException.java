package com.coding2.the.max.petstore.order.exception;

/**
 * Exception thrown when an order cannot be found
 */
public class OrderNotFoundException extends Exception {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
