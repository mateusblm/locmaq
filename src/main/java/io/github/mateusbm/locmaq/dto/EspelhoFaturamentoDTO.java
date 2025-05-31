package io.github.mateusbm.locmaq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspelhoFaturamentoDTO {
    private String cliente;
    private String equipamento;
    private LocalDate inicioLocacao;
    private LocalDate fimLocacao;
    private BigDecimal valorCliente;
    private BigDecimal valorDono;
    private BigDecimal taxaLucro;
    private String statusPagamento;
    private String contrato;
}