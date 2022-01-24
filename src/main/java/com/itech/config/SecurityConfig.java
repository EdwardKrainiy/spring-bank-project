package com.itech.config;

import com.itech.model.enumeration.Role;
import com.itech.security.jwt.filter.JwtAuthenticationFilter;
import com.itech.utils.literal.PropertySourceClasspath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
 *
 * @author Edvard Krainiy on 12/6/2021
 */

@Configuration
@EnableWebSecurity
@PropertySource({PropertySourceClasspath.SECURITY_PROPERTIES_CLASSPATH, PropertySourceClasspath.JWT_PROPERTIES_CLASSPATH})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService customUserDetailsService;
    @Value("${encrypt.rounds}")
    private int encryptRounds;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/email-confirmation").hasAuthority(Role.MANAGER.name())
                .antMatchers("/api/accounts/*").hasAnyAuthority(Role.USER.name(), Role.MANAGER.name())
                .antMatchers("/api/transactions/*").hasAnyAuthority(Role.MANAGER.name(), Role.USER.name())
                .antMatchers(HttpMethod.GET, "/api/*/creation-requests/*").hasAnyAuthority(Role.USER.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.GET, "/api/accounts/creation-requests/{\\\\d+}/**").hasAuthority(Role.MANAGER.name())
                .antMatchers("/*").permitAll()
                .anyRequest().permitAll();

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder());
    }

    @Bean
    protected PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(encryptRounds);
    }

    /**
     * Returns authenticationManagerBean() and adds this one to Application context.
     *
     * @return authenticationManagerBean
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Returns authenticationTokenFilterBean() and adds this one to Application context.
     *
     * @return JwtAuthenticationFilterBean
     */
    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationFilter();
    }
}
