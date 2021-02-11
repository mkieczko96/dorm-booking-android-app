package com.dorm.booker.api.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {

    private final Long expirationInMillis; //30 minutes by default
    private final UserDetailsService userDetailsService;
    private String secretKey;

    public JwtTokenService(@Value("${security.jwt.token.secret-key:secret}") String secretKey,
                           @Value("${security.jwt.token.expire-length:1800000}") Long expirationInMillis,
                           @Autowired @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.expirationInMillis = expirationInMillis;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<String> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("University of Technology")
                .setAudience("Booker")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String t) {
        UserDetails user = userDetailsService.loadUserByUsername(getUsername(t));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String getUsername(String t) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(t)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String t) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(t);
            System.out.println(t);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return true;
    }

}
