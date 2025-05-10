package com.coding2.the.max.petstore.order.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PaymentDetails;
import com.coding2.the.max.petstore.order.model.PetOrder;
import com.coding2.the.max.petstore.order.service.PetOrderService;

@RestController
@RequestMapping("/api/orders")
public class PetOrderController {

  private final PetOrderService petOrderService;

  public PetOrderController(PetOrderService petOrderService) {
    this.petOrderService = petOrderService;
  }

  @PostMapping
  public Mono<PetOrder> createOrder(@RequestBody PetOrder petOrder) {
    return petOrderService.createOrder(petOrder);
  }

  @GetMapping("/{orderId}")
  public Mono<PetOrder> getOrderById(@PathVariable String orderId) {
    return petOrderService.getOrderById(orderId);
  }

  @PutMapping("/{orderId}")
  public Mono<PetOrder> updateOrder(@PathVariable String orderId, @RequestBody PetOrder petOrder) {
    return petOrderService.updateOrder(orderId, petOrder);
  }

  @DeleteMapping("/{orderId}")
  public Mono<Boolean> cancelOrder(@PathVariable String orderId) {
    return petOrderService.cancelOrder(orderId);
  }

  @PostMapping("/{orderId}/payment")
  public Mono<PetOrder> processPayment(@PathVariable String orderId, @RequestBody PaymentDetails paymentDetails) {
    return petOrderService.processPayment(orderId, paymentDetails);
  }

  @PatchMapping("/{orderId}/status")
  public Mono<PetOrder> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatus status) {
    return petOrderService.updateOrderStatus(orderId, status);
  }

  @GetMapping("/customer/{customerId}")
  public Flux<PetOrder> findOrdersByCustomer(@PathVariable String customerId) {
    return petOrderService.findOrdersByCustomer(customerId);
  }

  @GetMapping("/status/{status}")
  public Flux<PetOrder> findOrdersByStatus(@PathVariable OrderStatus status) {
    return petOrderService.findOrdersByStatus(status);
  }
}