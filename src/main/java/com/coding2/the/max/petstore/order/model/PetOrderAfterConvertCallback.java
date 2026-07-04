package com.coding2.the.max.petstore.order.model;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class PetOrderAfterConvertCallback implements AfterConvertCallback<PetOrder> {

  @Override
  @NonNull
  public Publisher<PetOrder> onAfterConvert(@NonNull PetOrder entity, @NonNull SqlIdentifier table) {
    entity.setNewEntity(false);
    return Mono.just(entity);
  }
}
