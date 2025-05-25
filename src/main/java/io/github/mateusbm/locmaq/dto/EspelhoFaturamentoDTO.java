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
    private BigDecimal valorTotal;
    private String statusPagamento;
    private String contrato;

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getEquipamento() { return equipamento; }
    public void setEquipamento(String equipamento) { this.equipamento = equipamento; }
    public LocalDate getInicioLocacao() { return inicioLocacao; }
    public void setInicioLocacao(LocalDate inicioLocacao) { this.inicioLocacao = inicioLocacao; }
    public LocalDate getFimLocacao() { return fimLocacao; }
    public void setFimLocacao(LocalDate fimLocacao) { this.fimLocacao = fimLocacao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }
    public String getContrato() { return contrato; }
    public void setContrato(String contrato) { this.contrato = contrato; }
}