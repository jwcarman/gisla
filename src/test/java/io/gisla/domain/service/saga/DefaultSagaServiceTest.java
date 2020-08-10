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

package io.gisla.domain.service.saga;

import java.util.List;

import com.google.gson.JsonPrimitive;
import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.command.StartSagaCommand;
import io.gisla.domain.value.TransactionDescriptor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSagaServiceTest {

    @Mock
    private CommandGateway gateway;

    @Captor
    private ArgumentCaptor<CreateSagaCommand> createCaptor;

    @Captor
    private ArgumentCaptor<StartSagaCommand> startCaptor;

    @Test
    void newSaga() {
        final DefaultSagaService service = new DefaultSagaService(gateway);
        when(gateway.sendAndWait(any(CreateSagaCommand.class))).thenReturn("12345");
        service.newSaga(List.of(
                TransactionDescriptor.builder()
                        .transactionType("foo")
                        .transactionSpec(new JsonPrimitive("foo"))
                        .build(),
                TransactionDescriptor.builder()
                        .transactionType("bar")
                        .transactionSpec(new JsonPrimitive("bar"))
                        .build()
                )
        );

        verify(gateway).sendAndWait(createCaptor.capture());
        final CreateSagaCommand create = createCaptor.getValue();
        assertThat(create.getTransactions()).hasSize(2);

        verify(gateway).send(startCaptor.capture());
        final StartSagaCommand start = startCaptor.getValue();
        assertThat(start.getSagaId()).isEqualTo("12345");
    }

}