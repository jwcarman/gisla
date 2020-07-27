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

package io.gisla.web.config;

import com.google.gson.Gson;
import io.gisla.domain.service.saga.SagaService;
import io.gisla.web.DefaultSagaResource;
import io.gisla.web.SagasResource;
import io.gisla.web.mapping.WebMapper;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.jaxxy.gson.GsonMessageBodyProvider;
import org.jaxxy.logging.RequestLogFilter;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Bean
    public GsonMessageBodyProvider gsonMessageBodyProvider(Gson gson) {
        return new GsonMessageBodyProvider(gson);
    }

    @Bean
    public OpenApiFeature openApiFeature() {
        return new OpenApiFeature();
    }

    @Bean
    public RequestLogFilter requestLogFilter() {
        return RequestLogFilter.builder()
                .build();
    }

    @Bean
    public SagasResource sagasResource(SagaService sagaService, WebMapper webMapper) {
        return new DefaultSagaResource(sagaService, webMapper);
    }

    @Bean
    WebMapper webMapper() {
        return Mappers.getMapper(WebMapper.class);
    }
}
