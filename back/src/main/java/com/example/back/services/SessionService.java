package com.example.back.services;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public SessionService(FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public int invalidateAllSessions(String email) {
        var sessions = sessionRepository.findByPrincipalName(email);
        sessions.keySet().forEach(sessionRepository::deleteById);
        return sessions.size();
    }
}
