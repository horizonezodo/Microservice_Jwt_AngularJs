package com.horizonezodo.accountservice.repo;

import com.horizonezodo.accountservice.model.RefreshToken;
import com.horizonezodo.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);

    @Modifying
    int deleteAllByUser(User user);
}
