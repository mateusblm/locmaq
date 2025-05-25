package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.EspelhoFaturamentoDTO;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspelhoFaturamentoService {

    @Autowired
    private ContratoLocacaoRepository contratoRepo;

    public List<EspelhoFaturamentoDTO> gerarEspelho() {
        return contratoRepo.findAll().stream().map(contrato -> {
            EspelhoFaturamentoDTO dto = new EspelhoFaturamentoDTO();
            dto.setCliente(contrato.getCliente().getNome());
            dto.setEquipamento(contrato.getEquipamento().getNome());
            dto.setInicioLocacao(contrato.getDataInicio());
            dto.setFimLocacao(contrato.getDataFim());
            dto.setValorTotal(BigDecimal.valueOf(contrato.getValorTotal()));
            dto.setStatusPagamento(contrato.getStatusPagamento() != null ? contrato.getStatusPagamento().toString() : "N/A");
            dto.setContrato("Contrato #" + contrato.getId());
            return dto;
        }).collect(Collectors.toList());
    }
}