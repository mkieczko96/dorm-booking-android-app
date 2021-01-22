package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import com.dormbooker.api.data.models.AuthenticatedUser;
import com.dormbooker.api.data.models.User;
import com.dormbooker.api.data.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private UserRepository repository;

    @Value("${jwt.secret}")
    private String secret;


    @PostMapping
    public ResponseEntity<AuthenticatedUser> retrieveJWT(@RequestHeader("username") String username,
                                                         @RequestHeader("password") String password) {
        User u = repository
                .findUserByUsername(username);

        if(u == null) return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(null);

        if (BCrypt.checkpw(password, u.getPassword())) {
            // Read encoded secret from application.properties and converts it to SecretKey
            byte[] skey = Decoders.BASE64.decode(secret);
            SecretKey key = Keys.hmacShaKeyFor(skey);
            Date issDate = new Date();
            Date expDate = new Date(System.currentTimeMillis() / 1000L + 12 * 60 * 60); // Expire after 12 hours
            String JWT = Jwts.builder()
                    .setIssuer(username)
                    .setIssuedAt(issDate)
                    .setNotBefore(issDate)
                    .setExpiration(expDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            AuthenticatedUser au = new AuthenticatedUser(u.getId(), u.getFirstName(), u.getRoom());

            return ResponseEntity
                    .ok()
                    .header("jwt-token", JWT)
                    .body(au);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }
}
