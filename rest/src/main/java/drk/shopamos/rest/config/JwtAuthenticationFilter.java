package drk.shopamos.rest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER_NAME);

        if(isNull(authHeader) || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            filterChain.doFilter(request,response);
            return;
        }

        final String jwtToken = authHeader.substring(AUTH_HEADER_PREFIX.length());
        final String username = jwtService.extractUsername(jwtToken);
        Logger.getLogger("meh").config(username);
    }
}
