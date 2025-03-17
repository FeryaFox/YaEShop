package ru.feryafox.authservice.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.feryafox.jwt.JwtUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String ipAddress = request.getRemoteAddr();

        log.info("Получен запрос от IP: {} с заголовком Authorization: {}", ipAddress, authHeader);

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                log.info("Попытка аутентификации по JWT: {}", token);

                if (jwtUtils.validateToken(token)) {
                    UUID userId = jwtUtils.getUserIdFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

                    log.info("Аутентифицирован пользователь: {} с ролями: {}",
                            userDetails.getUsername(),
                            userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(", ")));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            log.info("Передача запроса в следующий фильтр. IP: {}", ipAddress);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("Истекший JWT-токен от IP: {}", ipAddress, e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token", "The token has expired. Please log in again.");
        } catch (JwtException e) {
            log.warn("Некорректный JWT-токен от IP: {}", ipAddress, e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token", "Invalid token.");
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка UUID в токене от IP: {}", ipAddress, e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "invalid_uuid", "Invalid UUID format.");
        } catch (Exception e) {
            log.error("Ошибка обработки JWT-фильтра для IP: {}", ipAddress, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server_error", "Internal server error.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, message));
    }
}
