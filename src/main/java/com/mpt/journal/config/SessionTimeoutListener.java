package com.mpt.journal.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class SessionTimeoutListener implements HttpSessionListener {

    // Время бездействия: 3 минуты = 180 секунд
    private static final int MAX_INACTIVE_INTERVAL = 180;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // Устанавливаем таймаут при создании сессии
        event.getSession().setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
        System.out.println("Сессия создана. Timeout: " + MAX_INACTIVE_INTERVAL + " секунд");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Сессия уничтожена: " + event.getSession().getId());
    }
}