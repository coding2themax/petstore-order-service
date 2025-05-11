package com.coding2.the.max.petstore.order.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(OrderRepository.class) // Import the repository if needed
class OrderRepositoryTest {

  @Autowired
  private OrderRepository orderRepository;

  @Test
  void testSaveAndFindById() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder
        .setOrderDate(LocalDate.now());
    petOrder.setStatus(OrderStatus.CREATED);

    Mono<PetOrder> saveAndFind = orderRepository.save(petOrder)
        .flatMap(savedOrder -> orderRepository.findById(savedOrder.getOrderId()));

    StepVerifier.create(saveAndFind)
        .assertNext(foundOrder -> {
          assertThat(foundOrder.getOrderId()).isEqualTo("1");
          assertThat(foundOrder.getCustomerId()).isEqualTo("123");
          assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        })
        .verifyComplete();
  }

  @Test
  void testFindByCustomerId() {
    PetOrder petOrder1 = new PetOrder();
    petOrder1.setOrderId("1");
    petOrder1.setCustomerId("123");
    petOrder1
        .setOrderDate(LocalDate.now());
    petOrder1.setStatus(OrderStatus.CREATED);

    PetOrder petOrder2 = new PetOrder();
    petOrder2.setOrderId("2");
    petOrder2.setCustomerId("123");
    petOrder2
        .setOrderDate(LocalDate.now());
    petOrder2.setStatus(OrderStatus.SHIPPED);

    Flux<PetOrder> saveAndFind = orderRepository.saveAll(List.of(petOrder1, petOrder2))
        .thenMany(orderRepository.findByCustomerId("123"));

    StepVerifier.create(saveAndFind)
        .expectNextMatches(order -> order.getOrderId().equals("1"))
        .expectNextMatches(order -> order.getOrderId().equals("2"))
        .verifyComplete();
  }

  @Test
  void testFindByStatus() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder
        .setOrderDate(LocalDate.now());
    petOrder.setStatus(OrderStatus.CREATED);

    Flux<PetOrder> saveAndFind = orderRepository.save(petOrder)
        .thenMany(orderRepository.findByStatus(OrderStatus.CREATED));

    StepVerifier.create(saveAndFind)
        .assertNext(order -> {
          assertThat(order.getOrderId()).isEqualTo("1");
          assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        })
        .verifyComplete();
  }
}