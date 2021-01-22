package com.dormbooker.api.data.controllers;

import com.dormbooker.api.security.jwt.JwtTokenService;
import com.dormbooker.api.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity authenticate(@RequestHeader("Authorization") String basicToken) {
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

//    @Value("${security.jwt.token.secret-key}")
//    private String secret;

//    @PostMapping
//    public ResponseEntity<User> retrieveJWT(@RequestHeader("username") String username,
//                                            @RequestHeader("password") String password) {
//        User u = repository
//                .findUserByUsername(username)
//                .orElseThrow();
//
//        if (u == null) return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(null);
//
//        if (BCrypt.checkpw(password, u.getPassword())) {
//            // Read encoded secret from application.properties and converts it to SecretKey
//            byte[] skey = Decoders.BASE64.decode(secret);
//            SecretKey key = Keys.hmacShaKeyFor(skey);
//            Date issDate = new Date();
//            Date expDate = new Date(System.currentTimeMillis() / 1000L + 12 * 60 * 60); // Expire after 12 hours
//            String JWT = Jwts.builder()
//                    .setIssuer(username)
//                    .setIssuedAt(issDate)
//                    .setNotBefore(issDate)
//                    .setExpiration(expDate)
//                    .signWith(key, SignatureAlgorithm.HS256)
//                    .compact();
//
//            return ResponseEntity
//                    .ok()
//                    .header("jwt-token", JWT)
//                    .body(u);
//        } else {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(null);
//        }
//    }
}
