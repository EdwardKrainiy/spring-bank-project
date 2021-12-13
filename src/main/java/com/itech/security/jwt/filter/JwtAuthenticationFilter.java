package com.itech.security.jwt.filter;

import com.itech.security.jwt.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthenticationFilter class. Provides us filtering our token in HTTP request and authenticating user, which was coded in transferred token.
 * @author Edvard Krainiy on 12/10/2021
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Value("${jwt.signing.key}")
    public String SIGN_KEY;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    /**
     * JwtFilter method.
     * @param req HTTP request, which contains our token.
     * @param res HTTP response, which will contain authorized user.
     * @param chain Contains list of filters, which will be applied.
     * @throws IllegalArgumentException If token wasn't fetched because of incorrect key.
     * @throws ExpiredJwtException If token is expired.
     * @throws SignatureException If username or password is not valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(TOKEN_PREFIX)) {

            authToken = header.replace(TOKEN_PREFIX, "");

            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken, SIGN_KEY);

            } catch (IllegalArgumentException e) {
                logger.error("An error occurred while fetching Username from Token", e);
                ResponseEntity.status(401).body("Token is not valid!");

            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired", e);
                ResponseEntity.status(401).body("Token is expired!");

            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
                ResponseEntity.status(401).body("Username or Password not valid!");
            }
        } else {
            logger.warn("Couldn't find bearer string, header will be ignored");

        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails, SIGN_KEY)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken, userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
        ResponseEntity.ok("Successfully signing-in!");
    }
}
