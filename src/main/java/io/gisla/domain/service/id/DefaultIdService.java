package io.gisla.domain.service.id;

import java.util.UUID;

public class DefaultIdService implements IdService {
    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
