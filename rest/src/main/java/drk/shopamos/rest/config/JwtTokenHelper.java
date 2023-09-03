package drk.shopamos.rest.config;

import static java.util.Objects.isNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class JwtTokenHelper {
    private final SecretKey signKey;
    private final Integer expirationSeconds;
    private final Clock clock;

    JwtTokenHelper(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.expiration_seconds}") Integer expirationSeconds,
            @Autowired Clock clock) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        if (isNull(userDetails)) {
            throw new IllegalArgumentException("userDetails cannot be null");
        }
        long currentMillis = clock.millis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentMillis))
                .setExpiration(new Date(currentMillis + expirationSeconds * 1000))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return new Date(clock.millis()).after(extractExpiration(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setClock(() -> new Date(clock.millis()))
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
