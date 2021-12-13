package com.itech.utils;

import com.itech.security.jwt.provider.TokenProvider;
import com.itech.utils.exception.ExpiredTokenException;
import com.itech.utils.exception.UserValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * getIdFromConfirmToken method. Gets id from transferred token.
     * @param token Transferred token of the user, whose id we need to obtain.
     * @return Long Obtained Id of User from token.
     * @throws ExpiredTokenException If token was expired.
     */
    public Long getIdFromConfirmToken(String token) throws ExpiredTokenException {

        Claims confirmationClaims = Jwts.parser().setSigningKey(CONFIRMATION_KEY).parseClaimsJws(token).getBody();

        if(tokenProvider.isTokenExpired(token, CONFIRMATION_KEY)){
            throw new ExpiredTokenException();
        }

        return Long.parseLong(confirmationClaims.getSubject());
    }
}
