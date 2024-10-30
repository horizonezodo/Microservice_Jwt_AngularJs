package com.horizonezodo.accountservice.repo;

import com.horizonezodo.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
