package com.labcloud.sample.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado");
        }

        return authentication.getName();
    }

    /**
     * Retorna dados do JWT armazenados no request. Esses dados são injetados pelo
     * JwtAuthenticationFilter.
     */
    public static String getCurrentUserId() {
        Object userId = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
                .getAttribute("userId", 0);

        if (userId == null) {
            throw new RuntimeException("userId não encontrado no contexto");
        }

        return userId.toString();
    }

    public static String getCurrentTenantId() {
        Object tenantId = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
                .getAttribute("tenantId", 0);

        if (tenantId == null) {
            throw new RuntimeException("tenantId não encontrado no contexto");
        }

        return tenantId.toString();
    }

    public static String getCurrentUserRole() {
        Object role = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
                .getAttribute("role", 0);

        if (role == null) {
            throw new RuntimeException("role não encontrado no contexto");
        }

        return role.toString();
    }

}
