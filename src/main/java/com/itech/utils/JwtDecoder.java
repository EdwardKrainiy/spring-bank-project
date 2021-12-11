package com.itech.utils;

import com.itech.security.jwt.provider.TokenProvider;
import com.itech.utils.exception.ExpiredTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JwtDecoder class, which contains methods to decode the JWT and obtain claims we need.
 * @autor Edvard Krainiy on ${date}
 * @version 1.0
 */

@Component
public class JwtDecoder {
    @Value("${jwt.confirmation.key}")
    private String CONFIRMATION_KEY;

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * getIdFromConfirmToken method. Gets id from transferred token.
     * @param token Transferred token of the user, whose id we need to obtain.
     * @return Long Obtained Id of User from token.
     * @throws ExpiredTokenException
     */
    public Long getIdFromConfirmToken(String token){

        Claims confirmationClaims = Jwts.parser().setSigningKey(CONFIRMATION_KEY).parseClaimsJws(token).getBody();

        try {
            if(tokenProvider.isTokenExpired(token, CONFIRMATION_KEY)){
                throw new ExpiredTokenException();
            }
        } catch (ExpiredTokenException exception){
            exception.getStackTrace();
        }
        return Long.parseLong(confirmationClaims.getSubject());
    }
}
