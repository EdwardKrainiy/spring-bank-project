package com.itech.repository;

import com.itech.model.Role;
import com.itech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserByRole(Role role);

    @Modifying
    @Query("update User u set u.activated = true where u.id=?1")
    void activateUser(Long userId);
}
