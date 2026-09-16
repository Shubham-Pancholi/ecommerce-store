package com.ecommerce.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.store.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration 
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer :: disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**", "/actuator/**", "/error").permitAll()
                                               .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                                               .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                                               .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
                                               .requestMatchers(HttpMethod.POST, "/api/v1/products/*/reviews").authenticated()
                                               .requestMatchers("/api/v1/products/**").hasAuthority("ROLE_ADMIN")
                                               .requestMatchers("/api/v1/orders/user/me").authenticated()
                                               .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAuthority("ROLE_ADMIN")
                                               .requestMatchers(HttpMethod.POST, "/api/v1/images/upload").hasAuthority("ROLE_ADMIN")
                                               .requestMatchers("/api/v1/categories/**").hasAuthority("ROLE_ADMIN")
                                               .requestMatchers("/api/v1/user/me").authenticated()
                                               .requestMatchers("/api/v1/user/**").hasAuthority("ROLE_ADMIN")
                                               .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}