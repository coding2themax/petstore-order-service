package com.coding2.the.max.petstore.order.repo;

import com.coding2.the.max.petstore.order.exception.PaymentProcessingException;
import com.coding2.the.max.petstore.order.model.PaymentDetails;

/**
 * Interface for payment processing service
 */
public interface PaymentService {
  String processPayment(PaymentDetails paymentDetails) throws PaymentProcessingException;

  boolean processRefund(String orderId) throws PaymentProcessingException;
}