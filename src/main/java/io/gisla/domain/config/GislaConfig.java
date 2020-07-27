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

package io.gisla.domain.config;

import io.gisla.domain.service.id.DefaultIdService;
import io.gisla.domain.service.id.IdService;
import io.gisla.domain.service.saga.DefaultSagaService;
import io.gisla.domain.service.saga.SagaService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GislaConfig {

    @Bean
    public IdService idService() {
        return new DefaultIdService();
    }

    @Bean
    public SagaService sagaService(CommandGateway commandGateway) {
        return new DefaultSagaService(commandGateway);
    }
}
