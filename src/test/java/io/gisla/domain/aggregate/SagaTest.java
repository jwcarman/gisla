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
import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.event.SagaCreatedEvent;
import io.gisla.domain.service.id.IdService;
import io.gisla.domain.value.TransactionDescriptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaTest {

    private final AggregateTestFixture<Saga> fixture = new AggregateTestFixture<>(Saga.class);
    @Mock
    private IdService idService;

    @BeforeEach
    void registerInjectables() {
        fixture.registerInjectableResource(idService);
    }

    @Test
    void createSagaCommand() {
        when(idService.generateId()).thenReturn("12345");

        fixture.givenNoPriorActivity()
                .when(CreateSagaCommand.builder()
                        .transaction(TransactionDescriptor.builder()
                                .type("foo")
                                .spec(new JsonPrimitive("bar"))
                                .build())
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectEvents(SagaCreatedEvent.builder()
                        .sagaId("12345")
                        .transaction(TransactionDescriptor.builder()
                                .type("foo")
                                .spec(new JsonPrimitive("bar"))
                                .build())
                        .build())
                .expectState(saga -> {
                    assertThat(saga.getSagaId()).isEqualTo("12345");
                });
    }
}