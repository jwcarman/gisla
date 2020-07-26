package io.gisla.domain.event;

import java.util.List;

import io.gisla.domain.value.TransactionDescriptor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class SagaCreatedEvent {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    String sagaId;

    @Singular
    List<TransactionDescriptor> transactions;
}

