package io.gisla.domain.service.saga;

import java.util.List;

import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.command.StartSagaCommand;
import io.gisla.domain.value.TransactionDescriptor;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;

@RequiredArgsConstructor
public class DefaultSagaService implements SagaService {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final CommandGateway commandGateway;

//----------------------------------------------------------------------------------------------------------------------
// SagaService Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String newSaga(List<TransactionDescriptor> transactions) {
        final String sagaId = commandGateway.sendAndWait(CreateSagaCommand.builder()
                .transactions(transactions)
                .build());
        commandGateway.send(StartSagaCommand.builder()
                .sagaId(sagaId)
                .build());
        return sagaId;
    }
}
