package io.gisla.domain.service.tx;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {

    @Override
    public void executeTransaction(String type, JsonElement spec) {
        log.info("Executing transaction of type ");
    }
}
