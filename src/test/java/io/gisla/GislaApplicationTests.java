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

package io.gisla;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import io.gisla.domain.service.saga.SagaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GislaApplicationTests {

	private static final int GRPC_PORT = 8124;
	private static final int HTTP_PORT = 8024;

	@Autowired
	private SagaService sagaService;

	@Container
	public static final GenericContainer<?> axonServer = new GenericContainer<>("axoniq/axonserver")
			.withExposedPorts(HTTP_PORT, GRPC_PORT)
			.waitingFor(Wait.forHttp("/actuator/info").forPort(HTTP_PORT))
			.withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS));

	static {
		axonServer.start();
		System.setProperty("axon.axonserver.servers", String.format("localhost:%d", axonServer.getMappedPort(GRPC_PORT)));
	}

	@Test
	void contextLoads() {
		assertThat(sagaService).isNotNull();
	}
}
