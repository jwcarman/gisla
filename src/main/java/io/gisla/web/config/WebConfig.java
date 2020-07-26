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
