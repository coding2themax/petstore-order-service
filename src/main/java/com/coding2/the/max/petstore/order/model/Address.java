package com.coding2.the.max.petstore.order.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a shipping address
 */
@Builder
@Getter
@Setter
public class Address {
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private String country;
  private String phoneNumber;

  // Constructors, getters, setters
}