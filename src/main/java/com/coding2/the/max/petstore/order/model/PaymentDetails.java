package com.coding2.the.max.petstore.order.model;

import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents payment details for processing an order
 */
@Data
@Getter
@Setter
public class PaymentDetails {
  private String paymentMethodId;
  private String transactionId;
  private double amount;
  private String currency;
  private Date paymentDate;

  // Constructors, getters, setters
}
