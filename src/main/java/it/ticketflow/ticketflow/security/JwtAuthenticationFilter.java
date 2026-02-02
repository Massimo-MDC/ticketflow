package it.ticketflow.ticketflow.security;

import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.exception.UnauthorizedException;
import it.ticketflow.ticketflow.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Estrai l'header Authorization
        String authHeader = request.getHeader("Authorization");

        // Verifica presenza e formato dell'header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Estrai il token
        String token = authHeader.substring(7);

        try {
            // Valida il token
            jwtService.validateToken(token);

            // Estrai l'ID utente dal token
            String userId = jwtService.extractSubject(token);

            // Carica l'utente dal database
            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new UnauthorizedException("Utente non trovato"));

            // Crea l'oggetto Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            // Imposta l'Authentication nel SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // In caso di errore, non impostiamo l'autenticazione
            // Spring Security gestirà la risposta 401/403
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Escludi TUTTI gli endpoint pubblici dal filtro JWT
        return pathMatcher.match("/api/auth/**", path) ||
                pathMatcher.match("/api/events/public/**", path) ||
                pathMatcher.match("/api/categories/**", path) ||
                pathMatcher.match("/api/venues/**", path) ||
                pathMatcher.match("/api/artists/**", path) ||
                pathMatcher.match("/graphql", path) ||
                pathMatcher.match("/graphiql/**", path);
    }
}