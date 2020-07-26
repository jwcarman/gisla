package io.gisla.domain.value;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionDescriptor {
    String type;
    JsonElement spec;
}
