package com.itech.repository;

import com.itech.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA Account repository class.
 * @author Edvard Krainiy on 12/16/2021
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

}
