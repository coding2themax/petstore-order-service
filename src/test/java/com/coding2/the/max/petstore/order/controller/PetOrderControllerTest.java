package com.coding2.the.max.petstore.order.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToApplicationContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.coding2.the.max.petstore.order.model.OrderStatus;
import com.coding2.the.max.petstore.order.model.PaymentDetails;
import com.coding2.the.max.petstore.order.model.PaymentStatus;
import com.coding2.the.max.petstore.order.model.PetOrder;
import com.coding2.the.max.petstore.order.service.PetOrderService;

@WebFluxTest(PetOrderController.class)
class PetOrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PetOrderService petOrderService;

    @Test
    void testCreateOrder() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setCustomerId("123");

        when(petOrderService.createOrder(any(PetOrder.class))).thenReturn(Mono.just(petOrder));

        webTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(petOrder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetOrder.class)
                .isEqualTo(petOrder);

        verify(petOrderService, times(1)).createOrder(any(PetOrder.class));
    }

    @Test
    void testGetOrderById() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");

        when(petOrderService.getOrderById("1")).thenReturn(Mono.just(petOrder));

        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetOrder.class)
                .isEqualTo(petOrder);

        verify(petOrderService, times(1)).getOrderById("1");
    }

    @Test
    void testUpdateOrder() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setCustomerId("123");

        when(petOrderService.updateOrder(eq("1"), any(PetOrder.class))).thenReturn(Mono.just(petOrder));

        webTestClient.put()
                .uri("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(petOrder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetOrder.class)
                .isEqualTo(petOrder);

        verify(petOrderService, times(1)).updateOrder(eq("1"), any(PetOrder.class));
    }

    @Test
    void testCancelOrder() {
        when(petOrderService.cancelOrder("1")).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);

        verify(petOrderService, times(1)).cancelOrder("1");
    }

    @Test
    void testProcessPayment() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setPaymentStatus(PaymentStatus.PAID);

        PaymentDetails paymentDetails = new PaymentDetails();

        when(petOrderService.processPayment(eq("1"), any(PaymentDetails.class))).thenReturn(Mono.just(petOrder));

        webTestClient.post()
                .uri("/api/orders/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(paymentDetails)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetOrder.class)
                .isEqualTo(petOrder);

        verify(petOrderService, times(1)).processPayment(eq("1"), any(PaymentDetails.class));
    }

    @Test
    void testUpdateOrderStatus() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setStatus(OrderStatus.SHIPPED);

        when(petOrderService.updateOrderStatus(eq("1"), eq(OrderStatus.SHIPPED))).thenReturn(Mono.just(petOrder));

        webTestClient.patch()
                .uri("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(OrderStatus.SHIPPED)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetOrder.class)
                .isEqualTo(petOrder);

        verify(petOrderService, times(1)).updateOrderStatus(eq("1"), eq(OrderStatus.SHIPPED));
    }

    @Test
    void testFindOrdersByCustomer() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setCustomerId("123");

        when(petOrderService.findOrdersByCustomer("123")).thenReturn(Flux.just(petOrder));

        webTestClient.get()
                .uri("/api/orders/customer/123")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PetOrder.class)
                .hasSize(1)
                .contains(petOrder);

        verify(petOrderService, times(1)).findOrdersByCustomer("123");
    }

    @Test
    void testFindOrdersByStatus() {
        PetOrder petOrder = new PetOrder();
        petOrder.setOrderId("1");
        petOrder.setStatus(OrderStatus.CREATED);

        when(petOrderService.findOrdersByStatus(OrderStatus.CREATED)).thenReturn(Flux.just(petOrder));

        webTestClient.get()
                .uri("/api/orders/status/CREATED")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PetOrder.class)
                .hasSize(1)
                .contains(petOrder);

        verify(petOrderService, times(1)).findOrdersByStatus(OrderStatus.CREATED);
    }
}