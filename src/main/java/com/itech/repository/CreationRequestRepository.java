package com.itech.repository;

import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA CreationRequest repository class.
 *
 * @author Edvard Krainiy on 12/31/2021
 */
@Repository
public interface CreationRequestRepository extends JpaRepository<CreationRequest, Long> {
    Optional<CreationRequest> findCreationRequestById(Long id);

    Optional<CreationRequest> findCreationRequestsByCreationTypeAndId(CreationType creationType, Long id);

    Optional<CreationRequest> findCreationRequestsByCreationTypeAndIdAndUser(CreationType creationType, Long id, User user);

    List<CreationRequest> findCreationRequestsByCreationType(CreationType creationType);

    List<CreationRequest> findCreationRequestsByCreationTypeAndUser(CreationType creationType, User user);

    Optional<CreationRequest> findCreationRequestsByIdAndStatus(Long id, Status status);

    List<CreationRequest> findCreationRequestsByCreationTypeAndStatus(CreationType creationType, Status status);
}
