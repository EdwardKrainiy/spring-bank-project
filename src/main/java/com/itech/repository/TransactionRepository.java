package com.itech.repository;

import com.itech.model.entity.Transaction;
import com.itech.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Transaction repository class.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTransactionByIdAndUser(Long id, User user);

    List<Transaction> findTransactionsByUser(User user);
}
