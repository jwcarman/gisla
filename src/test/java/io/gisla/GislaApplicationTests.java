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
