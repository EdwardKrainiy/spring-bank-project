package com.itech.utils.mapper.request;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.entity.CreationRequest;
import com.itech.repository.CreationRequestRepository;
import com.itech.utils.mapper.operation.OperationDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/** @author Edvard Krainiy on 01/05/2022 */
@Mapper(
    componentModel = "spring",
    uses = {OperationDtoMapper.class, CreationRequestRepository.class})
public interface RequestDtoMapper {
  /**
   * toDto method. Converts CreationRequest object to CreationRequestDto.
   *
   * @param creationRequest CreationRequest object we need to convert.
   * @return CreationRequestDto Obtained CreationRequestDto entity.
   */
  @Mappings({
    @Mapping(source = "creationRequest.user.id", target = "userId"),
    @Mapping(source = "creationRequest.issuedAt", target = "issuedAt")
  })
  CreationRequestDto toDto(CreationRequest creationRequest);
}
