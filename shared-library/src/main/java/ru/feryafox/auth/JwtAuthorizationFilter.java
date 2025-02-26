package ru.feryafox.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.feryafox.jwt.JwtUtils;
import ru.feryafox.jwt.JwtUtilsBase;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtilsBase jwtUtils;

    public JwtAuthorizationFilter(JwtUtilsBase jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtils.validateToken(token)) {
                    UUID userId = jwtUtils.getUserIdFromToken(token);
                    List<String> roles = jwtUtils.getUserRolesFromToken(token);

                    log.info("Authenticated user: {} with roles: {}", userId, String.join(", ", roles));

                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    CustomUserDetails userDetails = new CustomUserDetails(userId.toString(), authorities);


                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            // Логируем, что в контексте Spring Security перед отправкой запроса дальше
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.info("Security Context: Principal={}, Authorities={}",
                        auth.getPrincipal(),
                        auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                );
            } else {
                log.warn("Security Context is EMPTY before proceeding!");
            }


            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("Expired token from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"invalid_token\", \"message\": \"The token has expired. Please log in again.\"}");
        } catch (JwtException e) {
            log.warn("Invalid token from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"invalid_token\", \"message\": \"Invalid token.\"}");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format in token from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"invalid_uuid\", \"message\": \"Invalid UUID format.\"}");
        }
    }
}
