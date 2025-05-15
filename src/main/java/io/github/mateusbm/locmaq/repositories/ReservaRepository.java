package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEquipamentoId(Long equipamentoId);
}