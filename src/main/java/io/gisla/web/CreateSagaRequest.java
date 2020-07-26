package io.gisla.web;

import java.util.List;

import io.gisla.web.dto.TransactionDescriptorDTO;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class CreateSagaRequest {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Singular
    List<TransactionDescriptorDTO> transactions;
}
