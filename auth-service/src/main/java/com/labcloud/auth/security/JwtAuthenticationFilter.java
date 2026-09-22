package com.labcloud.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extrair header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Verificar se tem Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extrair token
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            // 4. Se tem email e ainda não está autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 5. Carregar usuário
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 6. Validar token
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 7. Extrair dados do token
                    String tenantId = jwtService.extractTenantId(jwt);
                    String role = jwtService.extractRole(jwt);
                    String userId = jwtService.extractUserId(jwt);

                    log.debug("Token válido para: {} (tenant: {}, role: {})", userEmail, tenantId, role);

                    // 8. Criar autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    // 9. Adicionar detalhes da requisição
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Setar no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // 11. Adicionar dados no request (para uso posterior)
                    request.setAttribute("tenantId", tenantId);
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao processar token JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
