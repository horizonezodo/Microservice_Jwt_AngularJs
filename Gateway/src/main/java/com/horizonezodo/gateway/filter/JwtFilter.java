package com.horizonezodo.gateway.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    @Value("${secret}")
    private String SECRET;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/refresh-token",
            "/auth/check-token",
            "/public/**"
    );

    public JwtFilter() {
        super(Config.class);
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    private boolean validateToken(String token){
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

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestURI = exchange.getRequest().getURI().getPath();
            if(EXCLUDED_PATHS.stream().noneMatch(requestURI::startsWith)){
                String token = exchange.getRequest().getHeaders().getFirst("Authorization").substring(7);
                if(token == null || !validateToken(token)){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
