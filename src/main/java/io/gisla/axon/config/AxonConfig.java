package io.gisla.axon.config;

import com.google.gson.Gson;
import io.gisla.axon.io.GsonSerializer;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.RoutingStrategy;
import org.axonframework.commandhandling.distributed.UnresolvedRoutingKeyPolicy;
import org.axonframework.serialization.RevisionResolver;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AxonConfig implements BeanClassLoaderAware {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private ClassLoader classLoader;

//----------------------------------------------------------------------------------------------------------------------
// BeanClassLoaderAware Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Bean
    @Primary
    public Serializer gsonSerializer(Gson gson, RevisionResolver revisionResolver) {
        return GsonSerializer.builder()
                .gson(gson)
                .classLoader(classLoader)
                .revisionResolver(revisionResolver)
                .build();
    }

    @Bean
    public RoutingStrategy routingStrategy() {
        return new AnnotationRoutingStrategy(UnresolvedRoutingKeyPolicy.STATIC_KEY);
    }
}
