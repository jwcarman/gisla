package io.gisla.web;

import java.util.List;
import java.util.stream.Collectors;

import io.gisla.domain.service.saga.SagaService;
import io.gisla.domain.value.TransactionDescriptor;
import io.gisla.web.mapping.WebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSagaResource implements SagasResource{

//----------------------------------------------------------------------------------------------------------------------
// SagasResource Implementation
//----------------------------------------------------------------------------------------------------------------------

    private final SagaService sagaService;
    private final WebMapper webMapper;

    @Override
    public CreateSagaResponse createSaga(CreateSagaRequest request) {
        final List<TransactionDescriptor> transactions = request.getTransactions().stream()
                .map(webMapper::fromDto)
                .collect(Collectors.toList());
        final String sagaId = sagaService.newSaga(transactions);
        return CreateSagaResponse.builder()
                .sagaId(sagaId)
                .build();
    }
}
