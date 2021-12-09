package com.itech.utils;

import com.itech.security.jwt.provider.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtDecoder {
    @Value("${jwt.confirmation.key}")
    private String CONFIRMATION_KEY;

    @Autowired
    private TokenProvider tokenProvider;

    public Long getIdFromConfirmToken(String token){

        Claims confirmationClaims = Jwts.parser().setSigningKey(CONFIRMATION_KEY).parseClaimsJws(token).getBody();

        if(!tokenProvider.isTokenExpired(token, CONFIRMATION_KEY)){
            return Long.parseLong(confirmationClaims.getSubject());
        }
        else return null;
    }
}
