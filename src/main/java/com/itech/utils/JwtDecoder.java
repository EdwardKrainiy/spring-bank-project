package com.itech.utils;

import com.itech.utils.exception.UserIdNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JwtDecoder class, which contains methods to decode the JWT and obtain claims we need.
 * @author Edvard Krainiy on 12/11/2021
 */

@Component
public class JwtDecoder {
    @Value("${jwt.confirmation.key}")
    private String CONFIRMATION_KEY;

    /**
     * getIdFromConfirmToken method. Gets id from transferred token.
     * @param token Transferred token of the user, whose id we need to obtain.
     * @return Long Obtained Id of User from token.
     * @throws ExpiredJwtException If token was expired.
     */
    public Long getIdFromConfirmToken(String token) throws ExpiredJwtException {

        Claims confirmationClaims = Jwts.parser().setSigningKey(CONFIRMATION_KEY).parseClaimsJws(token).getBody();

        Long userId = Long.parseLong(confirmationClaims.getSubject());

        if(userId == null){
            throw new UserIdNotFoundException("Id not found!");
        }

        return userId;
    }
}
