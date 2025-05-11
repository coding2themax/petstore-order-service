package com.coding2.the.max.petstore.order.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import lombok.Data;
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
  private LocalDate orderDate;

  @MappedCollection(idColumn = "order_id")
  private List<OrderItem> items;

  @Column(value = "status")
  private OrderStatus status;

  @Column(value = "payment_status")
  private PaymentStatus paymentStatus;

  @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
  private Address shippingAddress;

  @Column(value = "total_amount")
  private double totalAmount;

  @Column(value = "special_instructions")
  private String specialInstructions;

  @Override
  @Nullable
  public String getId() {
    return this.orderId;
  }

  @Override
  public boolean isNew() {
    return this.orderId == null || this.orderId.isEmpty();
  }

}
