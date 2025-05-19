package io.github.mateusbm.locmaq.repositories;

import io.github.mateusbm.locmaq.models.ContratoLocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ContratoLocacaoRepository extends JpaRepository<ContratoLocacao, Long> {

    @Query("SELECT c FROM ContratoLocacao c WHERE c.equipamento.id = :equipamentoId AND " +
            "(:dataInicio BETWEEN c.dataInicio AND c.dataFim OR " +
            ":dataFim BETWEEN c.dataInicio AND c.dataFim OR " +
            "c.dataInicio BETWEEN :dataInicio AND :dataFim OR " +
            "c.dataFim BETWEEN :dataInicio AND :dataFim) AND " +
            "(:ignoreContratoId IS NULL OR c.id != :ignoreContratoId)")
    List<ContratoLocacao> findByEquipamentoIdAndPeriodExcluding(@Param("equipamentoId") Long equipamentoId,
                                                                @Param("dataInicio") LocalDate dataInicio,
                                                                @Param("dataFim") LocalDate dataFim,
                                                                @Param("ignoreContratoId") Long ignoreContratoId);
}