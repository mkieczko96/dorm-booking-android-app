package com.dorm.booker.api.data.controllers;

import com.dorm.booker.api.data.repositories.UserRepository;
import com.dorm.booker.api.security.jwt.JwtTokenService;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserRepository repository;

    private final JwtTokenService jwtTokenService;

    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<Map<Object, Object>> authenticate(@RequestHeader("Authorization") String basicToken) {
        try {
            String[] credentials = new String(Base64.getDecoder().decode(basicToken.substring(6))).split(":");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials[0], credentials[1]));
            UserDetails user = repository.findUserByUsername(credentials[0])
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + credentials[0] + " not found."));

            String token = jwtTokenService.createToken(credentials[0], user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            Map<Object, Object> body = new HashMap<>();
            body.put("username", user.getUsername());
            body.put("token", token);

            return ResponseEntity.ok(body);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid credentials supplied");
        }
    }
}
