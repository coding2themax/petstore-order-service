package com.coding2.the.max.petstore.order.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.coding2.the.max.petstore.order.exception.InvalidOrderException;
import com.coding2.the.max.petstore.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.petstore.order.exception.OrderNotFoundException;
import com.coding2.the.max.petstore.order.exception.PaymentProcessingException;
import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PaymentDetails;
import com.coding2.the.max.petstore.order.model.PaymentStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;
import com.coding2.the.max.petstore.order.model.PetOrderAfterConvertCallback;
import com.coding2.the.max.petstore.order.repo.OrderRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import({ PetOrderServiceImpl.class, PetOrderAfterConvertCallback.class })
@Testcontainers(disabledWithoutDocker = true)
class PetOrderServiceImplTest {

  @Container
  private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:15.2")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PetOrderServiceImpl petOrderService;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + POSTGRESQL_CONTAINER.getHost() + ":" +
        POSTGRESQL_CONTAINER.getMappedPort(5432) + "/" + POSTGRESQL_CONTAINER.getDatabaseName());
    registry.add("spring.r2dbc.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.r2dbc.password", POSTGRESQL_CONTAINER::getPassword);
    registry.add("spring.flyway.enabled", () -> false); // Disable Flyway if not used
  }

  @BeforeEach
  void setUp() {
    // Clean up the database before each test
    orderRepository.deleteAll().block();
  }

  @AfterEach
  void tearDown() {
    // Clean up the database after each test
    orderRepository.deleteAll().block();
  }

  @Test
  void testCreateOrder() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    Mono<PetOrder> result = petOrderService.createOrder(petOrder);

    StepVerifier.create(result)
        .assertNext(order -> {
          assertThat(order.getOrderId()).isEqualTo("1");
          assertThat(order.getCustomerId()).isEqualTo("123");
          assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        })
        .verifyComplete();
  }

  @Test
  void testGetOrderById() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    Mono<PetOrder> result = petOrderService.getOrderById("1");

    StepVerifier.create(result)
        .assertNext(order -> {
          assertThat(order.getOrderId()).isEqualTo("1");
          assertThat(order.getCustomerId()).isEqualTo("123");
        })
        .verifyComplete();
  }

  @Test
  void testGetOrderById_NotFound() {
    Mono<PetOrder> result = petOrderService.getOrderById("nonexistent");

    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof OrderNotFoundException &&
            throwable.getMessage().contains("Order not found with ID: nonexistent"))
        .verify();
  }

  @Test
  void testCancelOrder() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    Mono<Boolean> result = petOrderService.cancelOrder("1");

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();

    StepVerifier.create(orderRepository.findById("1"))
        .assertNext(order -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED))
        .verifyComplete();
  }

  @Test
  void testCreateOrder_InvalidData() {
    Mono<PetOrder> result = petOrderService.createOrder(new PetOrder());

    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof InvalidOrderException &&
            throwable.getMessage().equals("Invalid order data"))
        .verify();
  }

  @Test
  void testUpdateOrder() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    PetOrder updatedOrder = new PetOrder();
    updatedOrder.setCustomerId("456");
    updatedOrder.setOrderDate(LocalDateTime.now());
    updatedOrder.setStatus(OrderStatus.SHIPPED);

    StepVerifier.create(petOrderService.updateOrder("1", updatedOrder))
        .assertNext(order -> {
          assertThat(order.getOrderId()).isEqualTo("1");
          assertThat(order.getCustomerId()).isEqualTo("456");
          assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        })
        .verifyComplete();
  }

  @Test
  void testCancelOrder_AlreadyCanceled() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CANCELED);

    orderRepository.save(petOrder).block();

    StepVerifier.create(petOrderService.cancelOrder("1"))
        .expectErrorMatches(throwable -> throwable instanceof InvalidStatusChangeException &&
            throwable.getMessage().equals("Order is already canceled"))
        .verify();
  }

  @Test
  void testProcessPayment() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    PaymentDetails paymentDetails = new PaymentDetails();
    paymentDetails.setPaymentMethodId("card-1");

    StepVerifier.create(petOrderService.processPayment("1", paymentDetails))
        .assertNext(order -> assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PAID))
        .verifyComplete();
  }

  @Test
  void testProcessPayment_AlreadyPaid() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);
    petOrder.setPaymentStatus(PaymentStatus.PAID);

    orderRepository.save(petOrder).block();

    StepVerifier.create(petOrderService.processPayment("1", new PaymentDetails()))
        .expectErrorMatches(throwable -> throwable instanceof PaymentProcessingException &&
            throwable.getMessage().equals("Payment already processed"))
        .verify();
  }

  @Test
  void testUpdateOrderStatus() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    StepVerifier.create(petOrderService.updateOrderStatus("1", OrderStatus.SHIPPED))
        .assertNext(order -> assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED))
        .verifyComplete();
  }

  @Test
  void testFindOrdersByStatus() {
    PetOrder petOrder = new PetOrder();
    petOrder.setOrderId("1");
    petOrder.setCustomerId("123");
    petOrder.setOrderDate(LocalDateTime.now());
    petOrder.setStatus(OrderStatus.CREATED);

    orderRepository.save(petOrder).block();

    StepVerifier.create(petOrderService.findOrdersByStatus(OrderStatus.CREATED))
        .expectNextMatches(order -> order.getOrderId().equals("1"))
        .verifyComplete();
  }

  @Test
  void testFindOrdersByCustomer() {
    PetOrder petOrder1 = new PetOrder();
    petOrder1.setOrderId("1");
    petOrder1.setCustomerId("123");
    petOrder1.setOrderDate(LocalDateTime.now());
    petOrder1.setStatus(OrderStatus.CREATED);

    PetOrder petOrder2 = new PetOrder();
    petOrder2.setOrderId("2");
    petOrder2.setCustomerId("123");
    petOrder2.setOrderDate(LocalDateTime.now());
    petOrder2.setStatus(OrderStatus.SHIPPED);

    orderRepository.save(petOrder1).block();
    orderRepository.save(petOrder2).block();

    StepVerifier.create(petOrderService.findOrdersByCustomer("123"))
        .expectNextMatches(order -> order.getOrderId().equals("1"))
        .expectNextMatches(order -> order.getOrderId().equals("2"))
        .verifyComplete();
  }
}
