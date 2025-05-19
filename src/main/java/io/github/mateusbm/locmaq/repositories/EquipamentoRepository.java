package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
}