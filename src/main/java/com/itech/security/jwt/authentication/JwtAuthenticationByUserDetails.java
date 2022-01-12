package com.itech.security.jwt.authentication;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.security.jwt.provider.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * JwtAuthentication class.
 *
 * @author Edvard Krainiy on 12/10/2021
 */

@Component
public class JwtAuthenticationByUserDetails {

    private final AuthenticationManager authenticationManager;

    private final TokenProvider jwtTokenUtil;

    public JwtAuthenticationByUserDetails(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Authenticate method.
     *
     * @param userDto User object which we need to check and authenticate.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    public ResponseEntity<String> authenticate(UserSignInDto userDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateAuthToken(authentication);
        return ResponseEntity.ok(token);
    }
}
