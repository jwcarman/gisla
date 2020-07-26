package io.gisla.web.mapping;

import io.gisla.domain.value.TransactionDescriptor;
import io.gisla.web.dto.TransactionDescriptorDTO;
import org.mapstruct.Mapper;

@Mapper
public interface WebMapper {

    TransactionDescriptor fromDto(TransactionDescriptorDTO dto);
}
