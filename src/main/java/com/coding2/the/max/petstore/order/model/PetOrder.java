package com.coding2.the.max.petstore.order.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a pet order in the system
 */
@Data
@Getter
@Setter
@Table(name = "pet_order", schema = "petstore")
public class PetOrder implements Persistable<String> {
  @Id
  private String orderId;

  @Column(value = "customer_id")
  private String customerId;

  @Column(value = "order_date")
  private LocalDateTime orderDate;

  @Transient
  private List<OrderItem> items;

  @Column(value = "status")
  private OrderStatus status;

  @Column(value = "payment_status")
  private PaymentStatus paymentStatus;

  @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "shipping_address_")
  private Address shippingAddress;

  @Column(value = "total_amount")
  private double totalAmount;

  @Column(value = "special_instructions")
  private String specialInstructions;

  @Transient
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private boolean newEntity = true;

  @Override
  @Nullable
  public String getId() {
    return this.orderId;
  }

  @Override
  public boolean isNew() {
    return this.newEntity;
  }

}
