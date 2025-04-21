package io.github.mateusbm.locmaq.dto;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoLocacaoDTO {
    private Long id;
    private Long usuarioLogisticaId;
    private String usuarioLogisticaNome;
    private Long clienteId;
    private String clienteNome;
    private Long equipamentoId;
    private String equipamentoNome;
    private Double valorTotal;
    private LocalDate dataFim;
    private LocalDate dataInicio;

    public static ContratoLocacaoDTO fromEntity(ContratoLocacao c) {
        ContratoLocacaoDTO dto = new ContratoLocacaoDTO();
        dto.setId(c.getId());
        dto.setDataFim(c.getDataFim());
        dto.setDataInicio(c.getDataInicio());
        if (c.getUsuarioLogistica() != null) {
            dto.setUsuarioLogisticaId(c.getUsuarioLogistica().getId());
            dto.setUsuarioLogisticaNome(c.getUsuarioLogistica().getNome());
        }
        if (c.getCliente() != null) {
            dto.setClienteId(c.getCliente().getId());
            dto.setClienteNome(c.getCliente().getNome());
        }
        if (c.getEquipamento() != null) {
            dto.setEquipamentoId(c.getEquipamento().getId());
            dto.setEquipamentoNome(c.getEquipamento().getNome());
        }
        dto.setValorTotal(c.getValorTotal());
        return dto;
    }

    public ContratoLocacao toEntity(Usuario usuario,
                                    Cliente cliente,
                                    Equipamento equipamento) {
        ContratoLocacao c = new ContratoLocacao();
        c.setId(this.id);
        c.setUsuarioLogistica(usuario);
        c.setCliente(cliente);
        c.setEquipamento(equipamento);
        c.setValorTotal(this.valorTotal);
        c.setDataInicio(this.dataInicio);
        c.setDataFim(this.dataFim);
        return c;
    }
}