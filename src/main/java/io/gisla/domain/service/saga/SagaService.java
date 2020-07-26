package io.gisla.domain.service.saga;

import java.util.List;

import io.gisla.domain.value.TransactionDescriptor;

public interface SagaService {
    String newSaga(List<TransactionDescriptor> transactions);
}
