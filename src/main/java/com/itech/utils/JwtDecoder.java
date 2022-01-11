package com.itech.utils;

import com.itech.utils.exception.EntityNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JwtDecoder class, which contains methods to decode the JWT and obtain claims we need.
 *
 * @author Edvard Krainiy on 12/11/2021
 */

@Component
public class JwtDecoder {
    @Value("${jwt.confirmation.key}")
    private String confirmationKey;

    /**
     * getIdFromConfirmToken method. Gets id from transferred token.
     *
     * @param token Transferred token of the user, whose id we need to obtain.
     * @return Long Obtained Id of User from token.
     * @throws ExpiredJwtException If token was expired.
     */
    public Long getIdFromConfirmToken(String token) throws ExpiredJwtException {

        Claims confirmationClaims = Jwts.parser().setSigningKey(confirmationKey).parseClaimsJws(token).getBody();

        Optional<Long> userId = Optional.of(Long.parseLong(confirmationClaims.getSubject()));

        return userId.orElseThrow(() -> new EntityNotFoundException("Id not found"));
    }

    public String getUsernameOfLoggedUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
