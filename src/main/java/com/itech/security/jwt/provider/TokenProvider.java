package com.itech.security.jwt.provider;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements Serializable {

    @Value("${jwt.token.validity}")
    private long TOKEN_VALIDITY;

    @Value("${jwt.signing.key}")
    private String SIGNING_KEY;

    @Value("${jwt.confirmation.key}")
    private String CONFIRMATION_KEY;

    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;

    public String getUsernameFromToken(String token, String key) {
        return getClaimFromToken(token, Claims::getSubject, key);
    }

    public Date getExpirationDateFromToken(String token, String key) {
        return getClaimFromToken(token, Claims::getExpiration, key);
    }

    public String getSubjectFromToken(String token, String key) {
        return getClaimFromToken(token, Claims::getSubject, key);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String key) {
        final Claims claims = getAllClaimsFromToken(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token, String key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token, String key) {
        final Date expiration = getExpirationDateFromToken(token, key);
        return expiration.before(new Date());
    }

    public String generateAuthToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Дата создания токена
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY*1000)) //Дата истечения токена
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY) //Ключ расшифровки
                .compact();
    }

    public String generateConfirmToken(Long userId){
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY*100))
                .signWith(SignatureAlgorithm.HS256, CONFIRMATION_KEY)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails, String key) {
        final String username = getUsernameFromToken(token, key);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, key));
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

}
