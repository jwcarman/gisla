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
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final SagaService sagaService;
    private final WebMapper webMapper;

//----------------------------------------------------------------------------------------------------------------------
// SagasResource Implementation
//----------------------------------------------------------------------------------------------------------------------


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
