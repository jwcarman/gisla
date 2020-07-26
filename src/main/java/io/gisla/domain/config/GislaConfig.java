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
