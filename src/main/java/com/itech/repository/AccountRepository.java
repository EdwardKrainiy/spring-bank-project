package com.itech.repository;

import com.itech.model.entity.Account;
import com.itech.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Account repository class.
 *
 * @author Edvard Krainiy on 12/16/2021
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAll();

    List<Account> findAccountsByUser(User user);

    Optional<Account> findAccountByIdAndUser(Long id, User user);

    Optional<Account> findAccountById(Long id);

    void deleteAccountById(Long id);

    Optional<Account> findAccountByAccountNumber(String accountNumber);
}
