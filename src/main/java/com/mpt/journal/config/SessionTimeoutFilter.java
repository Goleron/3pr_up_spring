package com.mpt.journal.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionTimeoutFilter implements Filter {

    // Время бездействия: 3 минуты = 180 секунд
    private static final int MAX_INACTIVE_INTERVAL = 180;

    // Максимальное время жизни сессии: 15 минут = 900 секунд
    private static final int MAX_SESSION_LIFETIME = 900;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Пропускаем проверку для публичных страниц
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.equals("/login") || requestURI.equals("/register") ||
                requestURI.startsWith("/css") || requestURI.startsWith("/js")) {
            chain.doFilter(request, response);
            return;
        }

        if (session != null && httpRequest.getUserPrincipal() != null) {
            Long sessionCreationTime = (Long) session.getAttribute("sessionCreationTime");
            Long lastAccessTime = (Long) session.getAttribute("lastAccessTime");
            long currentTime = System.currentTimeMillis();

            // Устанавливаем время создания сессии при первом запросе
            if (sessionCreationTime == null) {
                session.setAttribute("sessionCreationTime", currentTime);
                sessionCreationTime = currentTime;
            }

            // Проверяем общее время жизни сессии (15 минут)
            long sessionLifetime = (currentTime - sessionCreationTime) / 1000;
            if (sessionLifetime > MAX_SESSION_LIFETIME) {
                System.out.println("Сессия истекла: превышено максимальное время жизни (15 минут)");
                session.invalidate();
                httpResponse.sendRedirect("/login?expired=true&reason=lifetime");
                return;
            }

            // Проверяем время бездействия (3 минуты)
            if (lastAccessTime != null) {
                long inactiveTime = (currentTime - lastAccessTime) / 1000;

                if (inactiveTime > MAX_INACTIVE_INTERVAL) {
                    System.out.println("Сессия истекла: превышено время бездействия (3 минуты)");
                    session.invalidate();
                    httpResponse.sendRedirect("/login?expired=true&reason=inactive");
                    return;
                }
            }

            // Обновляем время последней активности
            session.setAttribute("lastAccessTime", currentTime);

            // Устанавливаем таймаут
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
        }

        chain.doFilter(request, response);
    }
}