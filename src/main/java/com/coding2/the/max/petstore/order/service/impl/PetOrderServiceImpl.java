package com.coding2.the.max.petstore.order.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.coding2.the.max.petstore.order.exception.InvalidOrderException;
import com.coding2.the.max.petstore.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.petstore.order.exception.OrderNotFoundException;
import com.coding2.the.max.petstore.order.exception.PaymentProcessingException;
import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PaymentDetails;
import com.coding2.the.max.petstore.order.model.PaymentStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;
import com.coding2.the.max.petstore.order.repo.OrderRepository;
import com.coding2.the.max.petstore.order.service.PetOrderService;

@Service
public class PetOrderServiceImpl implements PetOrderService {

  private final OrderRepository orderRepository;

  public PetOrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Mono<PetOrder> createOrder(PetOrder petOrder) {
    if (petOrder == null || petOrder.getCustomerId() == null) {
      return Mono.error(new InvalidOrderException("Invalid order data"));
    }
    petOrder.setStatus(OrderStatus.CREATED);
    return orderRepository.save(petOrder).then(Mono.just(petOrder));
  }

  @Override
  public Mono<PetOrder> getOrderById(String orderId) {
    return orderRepository.findById(orderId)
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with ID: " + orderId)));
  }

  @Override
  public Mono<PetOrder> updateOrder(String orderId, PetOrder petOrder) {
    return getOrderById(orderId)
        .flatMap(existingOrder -> {
          petOrder.setOrderId(orderId);
          return orderRepository.save(petOrder).then(Mono.just(petOrder));
        });
  }

  @Override
  public Mono<Boolean> cancelOrder(String orderId) {
    return getOrderById(orderId)
        .flatMap(order -> {
          if (order.getStatus() == OrderStatus.CANCELED) {
            return Mono.error(new InvalidStatusChangeException("Order is already canceled"));
          }
          order.setStatus(OrderStatus.CANCELED);
          return orderRepository.save(order).thenReturn(true);
        });
  }

  @Override
  public Mono<PetOrder> processPayment(String orderId, PaymentDetails paymentDetails) {
    return getOrderById(orderId)
        .flatMap(order -> {
          if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return Mono.error(new PaymentProcessingException("Payment already processed"));
          }
          // Simulate payment processing logic here
          order.setPaymentStatus(PaymentStatus.PAID);
          return orderRepository.save(order).then(Mono.just(order));
        });
  }

  @Override
  public Mono<PetOrder> updateOrderStatus(String orderId, OrderStatus status) {
    return getOrderById(orderId)
        .flatMap(order -> {
          order.setStatus(status);
          return orderRepository.save(order).then(Mono.just(order));
        });
  }

  @Override
  public Flux<PetOrder> findOrdersByCustomer(String customerId) {
    return orderRepository.findByCustomerId(customerId);
  }

  @Override
  public Flux<PetOrder> findOrdersByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status);
  }
}