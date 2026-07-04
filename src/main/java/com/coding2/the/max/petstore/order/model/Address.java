package com.coding2.the.max.petstore.order.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a shipping address
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
  private String street;
  private String city;
  private String state;
  @Column("zip")
  private String zipCode;
  @Transient
  private String country;
  @Transient
  private String phoneNumber;
}