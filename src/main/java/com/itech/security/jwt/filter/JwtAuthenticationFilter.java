package com.itech.security.jwt.filter;

import com.itech.security.jwt.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
 *
 * @author Edvard Krainiy on 12/10/2021
 */

@PropertySource("classpath:properties/jwt.properties")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    private String headerString;

    @Value("${jwt.token.prefix}")
    private String tokenPrefix;

    @Value("${jwt.signing.key}")
    private String signKey;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    /**
     * JwtFilter method.
     *
     * @param req   HTTP request, which contains our token.
     * @param res   HTTP response, which will contain authorized user.
     * @param chain Contains list of filters, which will be applied.
     * @throws IllegalArgumentException If token wasn't fetched because of incorrect key.
     * @throws ExpiredJwtException      If token is expired.
     * @throws SignatureException       If username or password is not valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(headerString);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(tokenPrefix)) {

            authToken = header.replace(tokenPrefix, Strings.EMPTY);
            username = jwtTokenUtil.getUsernameFromToken(authToken, signKey);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(authToken, userDetails, signKey))) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken, userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}
