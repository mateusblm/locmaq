package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.EspelhoFaturamentoDTO;
import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.models.TipoOrcamento;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.repositories.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EspelhoFaturamentoService {

    @Autowired
    private ContratoLocacaoRepository contratoRepo;

    @Autowired
    private OrcamentoRepository orcamentoRepo;

    public List<EspelhoFaturamentoDTO> gerarEspelho() {
        return contratoRepo.findAll().stream().map(contrato -> {
            EspelhoFaturamentoDTO dto = new EspelhoFaturamentoDTO();
            dto.setCliente(contrato.getCliente().getNome());
            dto.setEquipamento(contrato.getEquipamento().getNome());
            dto.setInicioLocacao(contrato.getDataInicio());
            dto.setFimLocacao(contrato.getDataFim());

            // Busca orçamento CLIENTE
            Optional<Orcamento> orcClienteOpt = orcamentoRepo
                    .findFirstByContratoIdAndTipoOrcamentoAndStatusInOrderByDataCriacaoDesc(
                            contrato.getId(),
                            TipoOrcamento.CLIENTE,
                            List.of(StatusOrcamento.APROVADO, StatusOrcamento.PENDENTE)
                    );
            // Busca orçamento DONO
            Optional<Orcamento> orcDonoOpt = orcamentoRepo
                    .findFirstByContratoIdAndTipoOrcamentoAndStatusInOrderByDataCriacaoDesc(
                            contrato.getId(),
                            TipoOrcamento.DONO,
                            List.of(StatusOrcamento.APROVADO, StatusOrcamento.PENDENTE)
                    );

            BigDecimal valorCliente = orcClienteOpt.map(o -> BigDecimal.valueOf(o.getValorTotal())).orElse(BigDecimal.ZERO);
            BigDecimal valorDono = orcDonoOpt.map(o -> BigDecimal.valueOf(o.getValorTotal())).orElse(BigDecimal.ZERO);
            BigDecimal taxaLucro = orcClienteOpt.map(o -> BigDecimal.valueOf(o.getTaxaLucro())).orElse(BigDecimal.ZERO);

            dto.setValorCliente(valorCliente);
            dto.setValorDono(valorDono);
            dto.setTaxaLucro(taxaLucro);
            dto.setStatusPagamento(contrato.getStatusPagamento() != null ? contrato.getStatusPagamento().toString() : "N/A");
            dto.setContrato("Contrato #" + contrato.getId());
            return dto;
        }).collect(Collectors.toList());
    }
}