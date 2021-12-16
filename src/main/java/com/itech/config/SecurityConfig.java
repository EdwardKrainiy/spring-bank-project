package com.itech.config;

import com.itech.model.Role;
import com.itech.security.jwt.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class.
 * @author Edvard Krainiy on 12/6/2021
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/sign-in").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/sign-up").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/email-confirmation").hasAuthority(Role.MANAGER.name())
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").hasAuthority(Role.MANAGER.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder());
    }

    @Bean
    protected PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Returns authenticationManagerBean() and adds this one to Application context.
     * @return authenticationManagerBean
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Returns authenticationTokenFilterBean() and adds this one to Application context.
     * @return JwtAuthenticationFilterBean
     */
    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean(){
        return new JwtAuthenticationFilter();
    }
}
