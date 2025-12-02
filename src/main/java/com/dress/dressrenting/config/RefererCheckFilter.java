package com.dress.dressrenting.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class RefererCheckFilter extends OncePerRequestFilter {

    private final List<String> ALLOWED_DOMAINS = List.of(
            "test.weshare.az",
            "weshare.az"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api")) {

            String referer = request.getHeader("Referer");
            String origin = request.getHeader("Origin");

            boolean isAllowed = false;

            if (referer != null) {
                for (String domain : ALLOWED_DOMAINS) {
                    if (referer.contains(domain)) {
                        isAllowed = true;
                        break;
                    }
                }
            } else if (origin != null) {
                for (String domain : ALLOWED_DOMAINS) {
                    if (origin.contains(domain)) {
                        isAllowed = true;
                        break;
                    }
                }
            }

            if (!isAllowed) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Invalid Source");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
