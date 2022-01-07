package com.itech.repository;

import com.itech.model.entity.CreationRequest;
import com.itech.model.enumeration.CreationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * JPA CreationRequest repository class.
 *
 * @author Edvard Krainiy on 12/31/2021
 */
@Repository
public interface CreationRequestRepository extends JpaRepository<CreationRequest, Long> {
    Optional<CreationRequest> findCreationRequestById(Long id);

    Optional<CreationRequest> findCreationRequestsByCreationTypeAndId(CreationType creationType, Long id);

    List<CreationRequest> findCreationRequestsByCreationType(CreationType creationType);
}
