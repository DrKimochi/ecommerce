package drk.shopamos.rest.config;

import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public String extractUsername(String token) {
        return token;
    }

}
