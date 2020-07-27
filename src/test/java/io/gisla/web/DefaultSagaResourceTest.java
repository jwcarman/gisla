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

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonPrimitive;
import io.gisla.domain.service.saga.SagaService;
import io.gisla.web.dto.TransactionDescriptorDTO;
import io.gisla.web.mapping.WebMapper;
import io.gisla.web.test.JaxrsServerExtension;
import org.jaxxy.gson.GsonMessageBodyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.gisla.web.test.JaxrsServerExtension.jaxrsServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSagaResourceTest {

    private final WebMapper mapper = Mappers.getMapper(WebMapper.class);
    @Mock
    private SagaService sagaService;
    @RegisterExtension
    final JaxrsServerExtension server = jaxrsServer(SagasResource.class, () -> new DefaultSagaResource(sagaService, mapper))
            .withProvider(new GsonMessageBodyProvider());

    @Test
    void createSaga() {
        when(sagaService.newSaga(anyList())).thenReturn("12345");

        final CreateSagaRequest request = CreateSagaRequest.builder()
                .transaction(TransactionDescriptorDTO.builder()
                        .type("foo")
                        .spec(new JsonPrimitive("bar"))
                        .build())
                .build();

        final CreateSagaResponse response = webTarget()
                .path("sagas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), CreateSagaResponse.class);
        assertThat(response.getSagaId()).isEqualTo("12345");
    }

    private WebTarget webTarget() {
        return ClientBuilder.newClient()
                .register(new GsonMessageBodyProvider())
                .target(server.baseUrl());
    }

}