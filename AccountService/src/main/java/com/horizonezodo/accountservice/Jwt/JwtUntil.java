package com.horizonezodo.accountservice.Jwt;

import com.horizonezodo.accountservice.service.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUntil {
    @Value("${secret}")
    private String SECRET;

    @Value("${jwtExpirationMs}")
    private long jwtDurationMs;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    public String getInfoFromJwt(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String generateTokenFromData(String data){
        return Jwts.builder()
                .setSubject(data)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtDurationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtTokenForLoginUsingEmail(UserDetailImpl userDetail){
        return generateTokenFromData(userDetail.getEmail());
    }

    public String generateJwtTokenForLoginUsingPhone(UserDetailImpl userDetail){
        return generateTokenFromData(userDetail.getPhone());
    }

    public boolean validateJwtToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        }catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claimsJwt = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
