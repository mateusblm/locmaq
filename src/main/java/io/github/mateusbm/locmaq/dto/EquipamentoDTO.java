package io.github.mateusbm.locmaq.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EquipamentoDTO {
    private String nome;
    private String descricao;
    private BigDecimal valorLocacao;
    private boolean disponibilidade;
    private Long clienteId;
    private Long donoId;
}