package com.na.example.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenUtil implements Serializable {

    public static final long serialVersionUID = 1L;

    @Value("${app.jwtSecret}")
    private String secret;

    @Value("${app.jwtExpirationInMs}")
    private int expirationInMs;

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userPrincipal.getRoles());
        claims.put("accounts", userPrincipal.getAccounts());
        claims.put("organization", userPrincipal.getOrganization());

        String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());

        Date createdDate = new Date();
        Date expiryDate = new Date(createdDate.getTime() + expirationInMs);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userPrincipal.getUsername())
                   .setIssuedAt(createdDate)
                   .setExpiration(expiryDate)
                   .signWith(SignatureAlgorithm.HS256, encodedSecret)
                   .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(token).getBody();
    }

    public String getUsernameFromJWT(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromJWT(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromJWT(token);
        return expiration.before(new Date());
    }

    public boolean validateToken(String authToken) {
        String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
        try {
            Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }
}
