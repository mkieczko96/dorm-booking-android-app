package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import com.dormbooker.api.data.models.User;
import com.dormbooker.api.data.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository repository;

    @Value("${jwt.secret}")
    private String secret;


    @PostMapping
    public ResponseEntity<String> retrieveJWT(@RequestHeader("username") String username,
                                              @RequestHeader("password") String password)
            throws ResourceNotExistsException {
        User u = repository
                .findUserByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotExistsException("User: " + username + " not found."));

        if (u.getPassword().contentEquals(password)) {
            // Read encoded secret from application.properties and converts it to SecretKey
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Date issDate = new Date();
            Date expDate = new Date(System.currentTimeMillis() / 1000L + 12 * 60 * 60); // Expire after 12 hours
            String JWT = Jwts.builder()
                    .setIssuer(username)
                    .setIssuedAt(issDate)
                    .setNotBefore(issDate)
                    .setExpiration(expDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            return ResponseEntity
                    .ok()
                    .header("jwt-token", JWT)
                    .body("Success");
        } else {
            throw new ResourceNotExistsException("Incorrect password.");
        }
    }
}
