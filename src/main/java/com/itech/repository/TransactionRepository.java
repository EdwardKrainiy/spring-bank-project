package com.itech.repository;

import com.itech.model.entity.Transaction;
import com.itech.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Transaction repository class.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findTransactionByIdAndUser(Long id, User user);

  Optional<Transaction> findTransactionById(Long id);

  List<Transaction> findTransactionsByUser(User user);
}
