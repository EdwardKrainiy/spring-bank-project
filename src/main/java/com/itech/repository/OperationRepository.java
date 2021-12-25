package com.itech.repository;

import com.itech.model.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Edvard Krainiy on 12/24/2021
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
}
