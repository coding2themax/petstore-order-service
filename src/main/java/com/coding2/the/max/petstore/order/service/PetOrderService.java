package com.coding2.the.max.petstore.order.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.coding2.the.max.petstore.order.exception.InvalidOrderException;
import com.coding2.the.max.petstore.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.petstore.order.exception.OrderNotFoundException;
import com.coding2.the.max.petstore.order.exception.PaymentProcessingException;
import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PaymentDetails;
import com.coding2.the.max.petstore.order.model.PetOrder;

/**
 * Main interface for Pet Order Service Defines operations for managing pet
 * orders in a pet store system
 */
public interface PetOrderService {

  /**
   * Create a new pet order in the system
   * 
   * @param petOrder The order details to be created
   * @return A Mono emitting the created pet order with generated ID and status
   * @throws InvalidOrderException If the order data is invalid
   */
  Mono<PetOrder> createOrder(PetOrder petOrder);

  /**
   * Retrieve an existing pet order by its unique identifier
   * 
   * @param orderId The unique identifier of the order
   * @return A Mono emitting the pet order if found
   * @throws OrderNotFoundException If no order exists with the given ID
   */
  Mono<PetOrder> getOrderById(String orderId);

  /**
   * Update an existing pet order
   * 
   * @param orderId  The ID of the order to update
   * @param petOrder The updated order details
   * @return A Mono emitting the updated pet order
   * @throws OrderNotFoundException If no order exists with the given ID
   * @throws InvalidOrderException  If the updated order data is invalid
   */
  Mono<PetOrder> updateOrder(String orderId, PetOrder petOrder);

  /**
   * Cancel an existing pet order
   * 
   * @param orderId The ID of the order to cancel
   * @return A Mono emitting true if successfully canceled, false otherwise
   * @throws OrderNotFoundException       If no order exists with the given ID
   * @throws InvalidStatusChangeException If the order cannot be canceled due to
   *                                      its current status
   */
  Mono<Boolean> cancelOrder(String orderId);

  /**
   * Process payment for an order
   * 
   * @param orderId        The ID of the order to process payment for
   * @param paymentDetails The payment details
   * @return A Mono emitting the updated order with payment status
   * @throws OrderNotFoundException     If no order exists with the given ID
   * @throws PaymentProcessingException If payment processing fails
   */
  Mono<PetOrder> processPayment(String orderId, PaymentDetails paymentDetails);

  /**
   * Update the status of an order
   * 
   * @param orderId The ID of the order to update status
   * @param status  The new status to set
   * @return A Mono emitting the updated order with new status
   * @throws OrderNotFoundException       If no order exists with the given ID
   * @throws InvalidStatusChangeException If the status change is not allowed
   */
  Mono<PetOrder> updateOrderStatus(String orderId, OrderStatus status);

  /**
   * Find orders by customer ID
   * 
   * @param customerId The ID of the customer
   * @return A Flux emitting orders for the specified customer
   */
  Flux<PetOrder> findOrdersByCustomer(String customerId);

  /**
   * Find orders by status
   * 
   * @param status The order status to search for
   * @return A Flux emitting orders with the specified status
   */
  Flux<PetOrder> findOrdersByStatus(OrderStatus status);
}