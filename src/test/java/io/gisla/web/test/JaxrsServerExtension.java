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

package io.gisla.web.test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.testutil.common.TestUtil;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JaxrsServerExtension implements BeforeEachCallback, AfterEachCallback {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final Class<?> serviceInterface;
    private final Supplier<?> implementationSupplier;
    private final List<Object> providers = new LinkedList<>();
    private Server server;
    private String baseUrl;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    private JaxrsServerExtension(Class<?> serviceInterface, Supplier<?> implementationSupplier) {
        this.serviceInterface = serviceInterface;
        this.implementationSupplier = implementationSupplier;
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public static <I> JaxrsServerExtension jaxrsServer(Class<I> serviceInterface, Supplier<? extends I> serviceImplementationSupplier) {
        return new JaxrsServerExtension(serviceInterface, serviceImplementationSupplier);
    }

//----------------------------------------------------------------------------------------------------------------------
// AfterEachCallback Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        server.destroy();
    }

//----------------------------------------------------------------------------------------------------------------------
// BeforeEachCallback Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();

        baseUrl = String.format("http://localhost:%s/", TestUtil.getNewPortNumber(serviceInterface));
        factory.setAddress(baseUrl);
        factory.setProviders(providers);
        factory.setFeatures(List.of(new LoggingFeature()));
        factory.setResourceClasses(serviceInterface);
        factory.setResourceProvider(serviceInterface, new SingletonResourceProvider(implementationSupplier.get(), true));

        this.server = factory.create();
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public String baseUrl() {
        return baseUrl;
    }

    public JaxrsServerExtension withProvider(Object provider) {
        providers.add(provider);
        return this;
    }
}