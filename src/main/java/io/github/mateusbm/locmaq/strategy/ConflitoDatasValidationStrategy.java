package io.github.mateusbm.locmaq.strategy;

import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConflitoDatasValidationStrategy implements ContratoValidationStrategy {

    private final ContratoLocacaoRepository contratoRepo;

    public ConflitoDatasValidationStrategy(ContratoLocacaoRepository contratoRepo) {
        this.contratoRepo = contratoRepo;
    }

    @Override
    public void validate(ContratoLocacao contrato, boolean isUpdate) {
        Long idExcluir = isUpdate ? contrato.getId() : null;

        List<ContratoLocacao> conflitos = contratoRepo.findByEquipamentoIdAndPeriodExcluding(
                contrato.getEquipamento().getId(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                idExcluir
        );

        if (!conflitos.isEmpty()) {
            throw new IllegalStateException("O equipamento já está reservado para o período informado. Conflito com Contratos: " + conflitos.stream().map(c -> c.getId().toString()).reduce((a, b) -> a + ", " + b).orElse(""));
        }
    }
}