package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.models.TipoOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    Optional<Orcamento> findFirstByContratoIdAndStatusInOrderByDataCriacaoDesc(Long contratoId, List<StatusOrcamento> status);

    Optional<Orcamento> findFirstByContratoIdAndTipoOrcamentoAndStatusInOrderByDataCriacaoDesc(
            Long contratoId,
            TipoOrcamento tipoOrcamento,
            List<StatusOrcamento> status
    );}

