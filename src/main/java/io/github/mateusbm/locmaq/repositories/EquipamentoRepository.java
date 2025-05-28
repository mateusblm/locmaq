package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    long countByDonoId(Long id);
    // Buscar por nome (case insensitive)
    List<Equipamento> findByNomeIgnoreCase(String nome);

    // Buscar por disponibilidade
    List<Equipamento> findByDisponibilidade(boolean disponibilidade);

    // Buscar por cliente
    List<Equipamento> findByClienteId(Long clienteId);

    // Buscar por dono
    List<Equipamento> findByDonoId(Long donoId);

    // Buscar por nome contendo (filtro)
    List<Equipamento> findByNomeContainingIgnoreCase(String nome);

    // Buscar por nome e dono
    List<Equipamento> findByNomeIgnoreCaseAndDonoId(String nome, Long donoId);

    // Deletar por nome (case insensitive)
    @Transactional
    @Modifying
    @Query("DELETE FROM Equipamento e WHERE LOWER(e.nome) = LOWER(:nome)")
    int deleteByNome(String nome);

    // Buscar por id e dono
    Optional<Equipamento> findByIdAndDonoId(Long id, Long donoId);

    // Buscar todos disponíveis
    List<Equipamento> findByDisponibilidadeTrue();

    @Modifying
    @Transactional
    @Query("DELETE FROM Equipamento e WHERE LOWER(e.nome) = LOWER(:nome) AND e.dono.id = :donoId")
    int deleteByNomeAndDonoId(String nome, Long donoId);
}
