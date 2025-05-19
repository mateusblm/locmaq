package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}