package io.gisla.web;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateSagaResponse {
    String sagaId;
}
