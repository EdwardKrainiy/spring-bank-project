package com.itech.security.jwt.authentication;

import com.itech.model.dto.UserDto;
import com.itech.security.jwt.provider.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationByUserDetails {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    public ResponseEntity<?> authenticate(UserDto userDto){
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
