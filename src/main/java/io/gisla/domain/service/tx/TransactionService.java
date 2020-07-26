package io.gisla.domain.service.tx;

import com.google.gson.JsonElement;

public interface TransactionService {
    void executeTransaction(String type, JsonElement spec);
}
