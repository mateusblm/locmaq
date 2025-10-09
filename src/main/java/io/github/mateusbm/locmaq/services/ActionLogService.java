package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.events.LogAction;
import io.github.mateusbm.locmaq.models.ActionLog;
import io.github.mateusbm.locmaq.repositories.ActionLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    /**
     * Listener:
     * Este método é chamado automaticamente pelo Spring de forma assíncrona/desacoplada
     * sempre que um evento LogAction é publicado.
     */
    @EventListener
    public void handleLogAction(LogAction event) {
        ActionLog log = new ActionLog();
        log.setAction(event.action());
        log.setUser(event.user());
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(event.details());
        actionLogRepository.save(log);
    }
}