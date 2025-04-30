package com.coding2.the.max.petstore.order.service;

import java.util.List;

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
   * @return The created pet order with generated ID and status
   * @throws InvalidOrderException If the order data is invalid
   */
  PetOrder createOrder(PetOrder petOrder) throws InvalidOrderException;

  /**
   * Retrieve an existing pet order by its unique identifier
   * 
   * @param orderId The unique identifier of the order
   * @return The pet order if found
   * @throws OrderNotFoundException If no order exists with the given ID
   */
  PetOrder getOrderById(String orderId) throws OrderNotFoundException;

  /**
   * Update an existing pet order
   * 
   * @param orderId  The ID of the order to update
   * @param petOrder The updated order details
   * @return The updated pet order
   * @throws OrderNotFoundException If no order exists with the given ID
   * @throws InvalidOrderException  If the updated order data is invalid
   */
  PetOrder updateOrder(String orderId, PetOrder petOrder) throws OrderNotFoundException, InvalidOrderException;

  /**
   * Cancel an existing pet order
   * 
   * @param orderId The ID of the order to cancel
   * @return True if successfully canceled, false otherwise
   * @throws OrderNotFoundException       If no order exists with the given ID
   * @throws InvalidStatusChangeException If the order cannot be canceled due to
   *                                      its current status
   */
  boolean cancelOrder(String orderId) throws OrderNotFoundException, InvalidStatusChangeException;

  /**
   * Process payment for an order
   * 
   * @param orderId        The ID of the order to process payment for
   * @param paymentDetails The payment details
   * @return The updated order with payment status
   * @throws OrderNotFoundException     If no order exists with the given ID
   * @throws PaymentProcessingException If payment processing fails
   */
  PetOrder processPayment(String orderId, PaymentDetails paymentDetails)
      throws OrderNotFoundException, PaymentProcessingException;

  /**
   * Update the status of an order
   * 
   * @param orderId The ID of the order to update status
   * @param status  The new status to set
   * @return The updated order with new status
   * @throws OrderNotFoundException       If no order exists with the given ID
   * @throws InvalidStatusChangeException If the status change is not allowed
   */
  PetOrder updateOrderStatus(String orderId, OrderStatus status)
      throws OrderNotFoundException, InvalidStatusChangeException;

  /**
   * Find orders by customer ID
   * 
   * @param customerId The ID of the customer
   * @return List of orders for the specified customer
   */
  List<PetOrder> findOrdersByCustomer(String customerId);

  /**
   * Find orders by status
   * 
   * @param status The order status to search for
   * @return List of orders with the specified status
   */
  List<PetOrder> findOrdersByStatus(OrderStatus status);
}