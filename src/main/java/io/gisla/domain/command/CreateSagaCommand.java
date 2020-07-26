package io.gisla.domain.command;

import java.util.List;

import io.gisla.domain.value.TransactionDescriptor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class CreateSagaCommand {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Singular
    List<TransactionDescriptor> transactions;
}
