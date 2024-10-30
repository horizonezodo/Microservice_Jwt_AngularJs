package com.horizonezodo.accountservice.service;

import com.horizonezodo.accountservice.exception.RefreshTokenException;
import com.horizonezodo.accountservice.model.RefreshToken;
import com.horizonezodo.accountservice.repo.RefreshTokenRepo;
import com.horizonezodo.accountservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {

    @Autowired
    private RefreshTokenRepo repo;

    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private UserRepo userRepo;

    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepo.findById(userId).get());
        refreshToken.setExpiredDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = repo.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken updateRefreshToken(String token){
        Optional<RefreshToken> opt = repo.findByToken(token);
        if(opt.isPresent()){
            RefreshToken refreshToken = opt.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken = repo.save(refreshToken);
            return refreshToken;
        }
        return null;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiredDate().compareTo(Instant.now()) < 0) {
            repo.delete(token);
            throw new RefreshTokenException(token.getToken(), "821");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return repo.deleteAllByUser(userRepo.findById(userId).get());
    }
}
