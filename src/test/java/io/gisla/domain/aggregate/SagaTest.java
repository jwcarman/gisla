/*
 * Copyright (c) 2018 The Gisla Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gisla.domain.aggregate;

import com.google.gson.JsonPrimitive;
import io.gisla.domain.command.CompleteTransactionCommand;
import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.command.StartSagaCommand;
import io.gisla.domain.event.SagaCreatedEvent;
import io.gisla.domain.event.SagaStartedEvent;
import io.gisla.domain.event.TransactionCompletedEvent;
import io.gisla.domain.mapping.DomainMapper;
import io.gisla.domain.message.ExecuteTransactionMessage;
import io.gisla.domain.service.id.IdService;
import io.gisla.domain.service.tx.TransactionService;
import io.gisla.domain.value.PendingTransaction;
import io.gisla.domain.value.TransactionDescriptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaTest {

    private final AggregateTestFixture<Saga> fixture = new AggregateTestFixture<>(Saga.class);

    @Mock
    private IdService idService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void registerInjectables() {
        fixture.registerInjectableResource(idService);
        fixture.registerInjectableResource(transactionService);
        fixture.registerInjectableResource(Mappers.getMapper(DomainMapper.class));
    }

    @Test
    void createSagaCommand() {
        when(idService.generateId()).thenReturn("12345", "a");

        fixture.givenNoPriorActivity()
                .when(CreateSagaCommand.builder()
                        .transaction(TransactionDescriptor.builder()
                                .transactionType("foo")
                                .transactionSpec(new JsonPrimitive("bar"))
                                .build())
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectEvents(SagaCreatedEvent.builder()
                        .sagaId("12345")
                        .pendingTransaction(PendingTransaction.builder()
                                .transactionId("a")
                                .transactionType("foo")
                                .transactionSpec(new JsonPrimitive("bar"))
                                .build())
                        .build())
                .expectState(saga -> {
                    assertThat(saga.getSagaId()).isEqualTo("12345");
                });
    }

    @Test
    void startSagaCommand() {
        final JsonPrimitive spec = new JsonPrimitive("bar");
        fixture.given(SagaCreatedEvent.builder()
                .sagaId("12345")
                .pendingTransaction(PendingTransaction.builder()
                        .transactionId("a")
                        .transactionType("foo")
                        .transactionSpec(spec)
                        .build())
                .build())
                .when(StartSagaCommand.builder()
                        .sagaId("12345")
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectEvents(SagaStartedEvent.builder().build())
                .expectState(saga -> {
                    assertThat(saga.getSagaId()).isEqualTo("12345");
                    assertThat(saga.getCompletedTransactions()).isEmpty();
                    assertThat(saga.getPendingTransactions()).hasSize(1);
                });
        verify(transactionService).executeTransaction(ExecuteTransactionMessage.builder()
                .transactionSpec(spec)
                .sagaId("12345")
                .transactionId("a")
                .transactionType("foo")
                .build());
    }

    @Test
    void completeTransactionCommand() {
        final JsonPrimitive transactionSpec = new JsonPrimitive("bar");
        final JsonPrimitive compensationSpec = new JsonPrimitive("rab");
        fixture.given(SagaCreatedEvent.builder()
                .sagaId("12345")
                .pendingTransaction(PendingTransaction.builder()
                        .transactionId("a")
                        .transactionType("foo")
                        .transactionSpec(transactionSpec)
                        .build())
                .build())
                .when(CompleteTransactionCommand.builder()
                        .sagaId("12345")
                        .transactionId("a")
                        .compensationSpec(compensationSpec)
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectEvents(TransactionCompletedEvent.builder()
                        .transactionId("a")
                        .compensationSpec(compensationSpec)
                        .build())
                .expectState(saga -> {
                    assertThat(saga.getSagaId()).isEqualTo("12345");
                    assertThat(saga.getPendingTransactions()).isEmpty();
                    assertThat(saga.getCompletedTransactions()).hasSize(1);
                });
    }
}