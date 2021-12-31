package com.itech.repository;

import com.itech.model.entity.CreationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Edvard Krainiy on 12/31/2021
 */
@Repository
public interface CreationRequestRepository extends JpaRepository<CreationRequest, Long> {
}
