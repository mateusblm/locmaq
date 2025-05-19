package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.ActionLog;
import io.github.mateusbm.locmaq.repositories.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private ActionLogRepository actionLogRepository;

    @GetMapping
    public List<ActionLog> getAllLogs() {
        return actionLogRepository.findAll();
    }

    @GetMapping("/{id}")
    public ActionLog getLogDetail(@PathVariable Long id) {
        Optional<ActionLog> log = actionLogRepository.findById(id);
        return log.orElse(null);
    }
}