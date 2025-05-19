package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.ActionLog;
import io.github.mateusbm.locmaq.repositories.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActionLogService {

    @Autowired
    private ActionLogRepository actionLogRepository;

    public void logAction(String action, String user, String details) {
        ActionLog log = new ActionLog();
        log.setAction(action);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);
        actionLogRepository.save(log);
    }

    public void logAction(String action, String user) {
        logAction(action, user, null);
    }
}