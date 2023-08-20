package drk.shopamos.rest.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import drk.shopamos.rest.model.entity.User;
import drk.shopamos.rest.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";
    private final JwtTokenHelper jwtTokenHelper;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER_NAME);

        if (isNull(authHeader) || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(AUTH_HEADER_PREFIX.length());
        final String username = jwtTokenHelper.extractUsername(jwtToken);
        if (nonNull(username) && isNull(SecurityContextHolder.getContext().getAuthentication())) {
            User user = userService.loadUserByUsername(username);
            if (jwtTokenHelper.isTokenValid(jwtToken, user)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
