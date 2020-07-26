package io.gisla.web.dto;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionDescriptorDTO {
    String type;
    JsonElement spec;
}
